package sample1_basics;
// Calculate new gps location based on current and disired distance and angle
public class NewGeoPos {

	private double lat;
	private double lon;
	
	public NewGeoPos(double lat, double lon, double angle, double dis)
	{
		int R = 6378;
		double lat1 = Math.toRadians(lat); 
		double lon1 = Math.toRadians(lon); 
		double brng = Math.toRadians(angle);
		double lat2 = Math.asin( Math.sin(lat1)*Math.cos(dis/R) + Math.cos(lat1)*Math.sin(dis/R)*Math.cos(brng));

		double lon2 = lon1 + Math.atan2(Math.sin(brng)*Math.sin(dis/R)*Math.cos(lat1), Math.cos(dis/R)-Math.sin(lat1)*Math.sin(lat2));

		this.lat = Math.toDegrees(lat2);
		this.lon = Math.toDegrees(lon2);
	}
	
	public double getLat()
	{
		return lat;
	}
	
	public double getLon()
	{
		return lon;
	}
}
