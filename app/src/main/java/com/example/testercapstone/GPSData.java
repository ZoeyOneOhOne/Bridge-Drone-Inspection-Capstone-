package com.example.testercapstone;

public class GPSData {
	double lat;
	double lon;
	double dir;
	
	/**Constructs a GPSData
	 * 
	 * @param lat The latitude the photo was taken at
	 * @param lon The longitude the photo was taken at
	 * @param dir The direction the drone was facing when the photo was taken. 361 if no direction was stored.
	 */
	public GPSData(double lat,double lon,double dir) {
		this.lat = lat;
		this.lon = lon;
		this.dir = dir;
	}
	
	/**Outputs the information in the object as a String
	 * @return The latitude, longitude, and direction on a string divided into 3 lines.
	 */
	public String toString() {
		String out = "";
		out += "Latitude: " + lat + "\n";
		out += "Longitude: " + lon + "\n";
		out += "Direction: " + dir + "\n";
		return out;
	}
}
