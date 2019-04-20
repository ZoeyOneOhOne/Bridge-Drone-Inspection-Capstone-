package com.example.testercapstone;

public class GPSData {
	private double lat;
	private double lon;
	private double alt;
	private double dir;
	
	/**Returns the latitude recorded in the GPSData
	 * 
	 * @return The latitude in the GPSData
	 */
	public double getLatitude() {
		return lat;
	}
	
	/**Returns the longitude recorded in the GPSData
	 * 
	 * @return The longitude in the GPSData
	 */
	public double getLongitude() {
		return lon;
	}
	
	/**Returns the altitude recorded in the GPSData
	 * 
	 * @return The altitude in the GPSData. 0 if no data was recorded.
	 */
	public double getAltitude() {
		return alt;
	}
	
	/**Returns the direction the camera was facing
	 * 
	 * @return The direction the camera was facing, in degrees. Returns 361 if no data was recorded.
	 */
	public double getDirection() {
		return dir;
	}
	
	/**Constructs a GPSData
	 * 
	 * @param lat The latitude the photo was taken at.
	 * @param lon The longitude the photo was taken at.
	 * @param alt The altitude the photo was taken at. 0 if no altitude was stored.
	 * @param dir The direction the drone was facing when the photo was taken. 361 if no direction was stored.
	 */
	public GPSData(double lat,double lon,double alt,double dir) {
		this.lat = lat;
		this.lon = lon;
		this.alt = alt;
		this.dir = dir;
	}
	
	/**Outputs the information in the object as a String
	 * @return The latitude, longitude, altitude, and direction on a string divided into 4 lines.
	 */
	public String toString() {
		String out = "";
		out += "Latitude: " + lat + "\n";
		out += "Longitude: " + lon + "\n";
		out += "Altitude: " + alt + "\n";
		out += "Direction: " + dir + "\n";
		return out;
	}
}
