package com.example.testercapstone;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.common.IImageMetadata;
import org.apache.sanselan.formats.jpeg.JpegImageMetadata;
import org.apache.sanselan.formats.jpeg.xmp.JpegXmpRewriter;
import org.apache.sanselan.formats.tiff.TiffDirectory;
import org.apache.sanselan.formats.tiff.TiffField;
import org.apache.sanselan.formats.tiff.TiffImageMetadata;
import org.apache.sanselan.formats.tiff.TiffImageMetadata.GPSInfo;
import org.apache.sanselan.formats.tiff.constants.TagInfo;

public class DroneMeta {

	JpegXmpRewriter metaWrite;
	File file;

	/** Creates an instance of DroneMeta to read com.example.testercapstone.MetaData from 'file'
	* @param file The file to access metadata of*/
	public DroneMeta(File file) { 
		this.file = file;
		metaWrite = new JpegXmpRewriter();
	}

	/** Does a basic sanitization of the input to ensure that no '<' appear*
	 * @param input The string to be sanitized
	 * @return The sanitized string*/
	private String sanitize(String input) { 
		String[] sections = input.split("@");
		String partial = "";
		for (int i = 0; i < sections.length - 1; i++) {
			partial += sections[i] + "@@";
		}
		partial += sections[sections.length - 1];
		sections = partial.split("<");
		String output = "";
		for (int i = 0; i < sections.length - 1; i++) {
			output += sections[i] + "@r";
		}
		output += sections[sections.length - 1];
		return output;
	}

	/** Desanitizes a sanitized input to return the original string
	 * @param input The string to be desanitized
	 * @return The desanitized string*/
	private String desanitize(String input) { 
		String[] sections = input.split("[^@]@r");
		String partial = "";
		for (int i = 0; i < sections.length - 1; i++) {
			partial += sections[i] + "<";
		}
		partial += sections[sections.length - 1];
		sections = partial.split("@@");
		String output = "";
		for (int i = 0; i < sections.length - 1; i++) {
			output += sections[i] + "@";
		}
		output += sections[sections.length - 1];
		return output;
	}

	/** Writes the inspection ID to the inspectionID tag. Equivalent to writeTag with a tagname of "inspectionID".
	 * @param ID The inspection ID to be written to the photo*/
	public void writeInspID(int ID) throws ImageReadException, ImageWriteException, FileNotFoundException, IOException { 
		writeTag("inspectionID", ID + "");
	}

	/** Reads the inspection ID from the inspectionID tag. Equivalent to readTag with a tagname of "inspectionID".
	 * @return The inspection ID of the photo*/
	public int readInspID() throws NumberFormatException, ImageReadException, IOException { 
		return Integer.parseUnsignedInt(readTag("inspectionID"));
	}

	/** Writes a comment to the comment tag. Equivalent to writeTag with a tagname of "comment".
	 * @param comment The text to write as a comment to the photo*/
	public void writeComment(String comment)
			throws ImageReadException, ImageWriteException, FileNotFoundException, IOException { 
		writeTag("comment", comment);
	}

	/** Reads a comment from the comment tag. Equivalent to readTag with a tagname of "comment".
	 * @return The text of the comment stored on the photo*/
	public String readComment() throws ImageReadException, IOException { 
		return readTag("comment");
	}

	/** Writes a tag with the given tagname that contains the given data
	 * @param tagname The name of the tag to write information to
	 * @param value The content to write to the tag*/
	public void writeTag(String tagname, String value)
			throws ImageReadException, ImageWriteException, FileNotFoundException, IOException { 
		if (value.contains("<"))
			sanitize(value);
		String xml = Sanselan.getXmpXml(file);
		if (xml == null)
			xml = "";
		String newXml;
		if (xml.contains("<" + tagname + ">")) {
			String start = xml.split("<" + tagname + ">")[0];
			String end;
			String[] last = xml.split("</" + tagname + ">");
			if (last.length > 1) {
				end = last[1];
			} else
				end = "";
			newXml = start + "<" + tagname + ">" + value + "</" + tagname + ">" + end;
		} else {
			newXml = xml + "<" + tagname + ">" + value + "</" + tagname + ">";
		}
		metaWrite.updateXmpXml(file, new FileOutputStream(file.getPath() + ".temp"), newXml);
		Files.move(Paths.get(file.getPath() + ".temp"), Paths.get(file.getPath()), StandardCopyOption.REPLACE_EXISTING);
	}

	/** Reads data from the given tagname
	 * @param tagname The name of the tag to read data from
	 * @return The data stored in the tag as a String*/
	public String readTag(String tagname) throws ImageReadException, IOException { 
		String xml = Sanselan.getXmpXml(file);
		String comment;
		if (xml != null && xml.contains("<" + tagname + ">")) {
			comment = xml.split("<" + tagname + ">")[1].split("</" + tagname + ">")[0];
			comment = desanitize(comment);
		} else {
			comment = "";
		}
		return comment;

	}

	/** Gets GPS data from the photo and returns it as a GPSData object
	 * @return A GPSData object containing the GPS information stored on the photo*/
	public GPSData getGPS() { 
		try {
			IImageMetadata imgMeta = Sanselan.getMetadata(file);
			if(imgMeta == null)
				return null;
			JpegImageMetadata jpgMeta = (JpegImageMetadata) imgMeta;
			TiffImageMetadata jpgExif = jpgMeta.getExif();
			GPSInfo jpgGPS = jpgExif.getGPS();
			TiffDirectory GPSdir = jpgExif.findDirectory(TiffImageMetadata.DIRECTORY_TYPE_GPS);
			TagInfo dirTag = new TagInfo("GPSImgDirection", 17, TagInfo.FIELD_TYPE_RATIONAL);
			TiffField imgDir = GPSdir.findField(dirTag);
			double dirVal;
			if (imgDir != null)
				dirVal = imgDir.getDoubleValue();
			else
				dirVal = 361;
			TagInfo altTag = new TagInfo("GPSAltitude", 17, TagInfo.FIELD_TYPE_RATIONAL);
			TiffField imgAlt = GPSdir.findField(altTag);
			double altVal;
			if (imgAlt != null)
				altVal = imgDir.getDoubleValue();
			else
				altVal = 0;
			TagInfo refTag = new TagInfo("GPSAltitudeRef", 17, TagInfo.FIELD_TYPE_BYTE);
			TiffField altRef = GPSdir.findField(refTag);
			int refVal;
			if (altRef != null)
				refVal = imgDir.getIntValue();
			else
				refVal = 0;
			double alt;
			if(refVal == 0)
				alt = altVal;
			else alt = altVal * -1;
			double lat = jpgGPS.getLatitudeAsDegreesNorth();
			double lon = jpgGPS.getLongitudeAsDegreesEast();
			GPSData data = new GPSData(lat, lon, alt, dirVal);
			return data;
		} catch (ImageReadException e) {
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
