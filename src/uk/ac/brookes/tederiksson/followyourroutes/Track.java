package uk.ac.brookes.tederiksson.followyourroutes;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import android.location.Location;
import android.text.format.DateFormat;
import android.util.Log;

public class Track {
	private String userID = "NOTSET", name, xml;
	private ArrayList<TrackSeg> trackSegs;

	public Track(String xml) {
		trackSegs = new ArrayList<Track.TrackSeg>();
		this.xml = xml;
		parseXml(xml);
	}
	
	public Track() {
		trackSegs = new ArrayList<Track.TrackSeg>();
		this.xml = "";  
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setUserID(String userID) {
		this.userID = userID;
	}
	
	public void addPoint(Location location) {
		if(trackSegs.size() == 0 || trackSegs.get(trackSegs.size()-1).getPoints().size()>20) {
			TrackSeg seg = new TrackSeg();
			seg.addPoint(location);
			trackSegs.add(seg);
		} else {
			trackSegs.get(trackSegs.size()-1).addPoint(location);
		}
	}
	
	public String getXml() {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"><brookesml><user-id>"+userID+"</user-id><date>ISO 8601 format date</date><trk><name>"
	+name+"</name>";
		for(TrackSeg seg : trackSegs) {
			xml += "<trkseg>";
			xml += seg.getXml();
			xml += "</trkseg>";
		}
		xml += "</trk></brookesml>";
		return xml;
	}

	public ArrayList<TrackSeg> getTrackSegs() {
		return trackSegs;
	}
	
	private void parseXml(String xml) {
		try {
			String[] currentSplit = xml.split("<user-id>");
			currentSplit = currentSplit[1].split("</user-id>");
			userID = currentSplit[0];
			currentSplit = currentSplit[1].split("<name>");
			currentSplit = currentSplit[1].split("</name>");
			name = currentSplit[0];
			
			boolean isFirst = true;
			for(String trackSegString : currentSplit[1].split("<trkseg>")) {
				if(isFirst) {
					isFirst = false;
					continue;
				}
				String[] split = trackSegString.split("</trkseg>");
				trackSegs.add(new TrackSeg(split[0]));
			}
		} catch(Exception e) {
			Log.e("Track", "Track failed to parse: " + xml);
		}
		
	}

	public String getUserID() {
		return userID;
	}

	public String getName() {
		return name;
	}
	
	public float getTopSpeed() {
		//Returns in m/s
		float fastest = 0;
		for(TrackSeg seg : trackSegs) {
			ArrayList<Location> points = seg.getPoints();
			
			for(int i = 0; i < points.size() - 1; i++) {
				float speed = getSpeedBetween(points.get(i), points.get(i+1));
				if(speed < 14 && speed > fastest)
					fastest = speed;
			}
		}
		return fastest;
	}
	
	public float getAverageSpeed() {
		//Returns in m/s
		float total = 0;
		int count = 0;
		for(TrackSeg seg : trackSegs) {
			ArrayList<Location> points = seg.getPoints();
			
			for(int i = 0; i < points.size() - 1; i++) {
				float speed = getSpeedBetween(points.get(i), points.get(i+1));
				if(speed < 14) {
					total += speed;
					count ++;
				}
			}
		}
		return (float) total/count;
	}
	
	public float getTotalDistance() {
		float distance = 0;
		for(TrackSeg seg : trackSegs) {
			ArrayList<Location> points = seg.getPoints();
			
			for(int i = 0; i < points.size() - 1; i++) {
				distance += getDistanceBetween(points.get(i), points.get(i+1));
			}
		}
		return distance;
		
	}
	
	public long getTime() {
		Log.d("Times", Long.toString(getLastLocation().getTime()) + " - " + Long.toString(getFirstLocation().getTime()) + " = " + Long.toString(getLastLocation().getTime() - getFirstLocation().getTime()));
		return  getLastLocation().getTime() - getFirstLocation().getTime();
	}
	
	public static float getDistanceBetween(Location loc1, Location loc2) {
		//m
		return loc1.distanceTo(loc2);
		
	}
	
	public static float getSpeedBetween(Location loc1, Location loc2) {
//		Log.d("SPEED", Float.toString(getDistanceBetween(loc1, loc2) / (loc2.getTime() - loc1.getTime())/1000/60/60));
		float time = loc2.getTime() - loc1.getTime();
		time = time / 1000;
		Log.d("TIME", Float.toString(time));
		Log.d("DIST", Float.toString(getDistanceBetween(loc1, loc2)));
		Log.d("SPEED", Float.toString(getDistanceBetween(loc1, loc2) / time));
		return (getDistanceBetween(loc1, loc2) / time);
	}
	
	public static LatLng LocationToLatLng(Location loc) {
		return new LatLng(loc.getLatitude(), loc.getLongitude());
	}
	
	public Location getFirstLocation() {
		return trackSegs.get(0).getPoints().get(0);
	}
	
	public Location getLastLocation() {
		return trackSegs.get(trackSegs.size()-1).getLastPoint();
	}
	
	public PolylineOptions getPolyLineOptions() {
		PolylineOptions options = new PolylineOptions();
		for (TrackSeg seg : trackSegs) {
			for (Location location : seg.getPoints()) {
				options.add(LocationToLatLng(location));
				Log.d("LATLNG", location.getLatitude()+", "+ location.getLongitude());
			}
		}
		
		return options;
	}
	
	private class TrackSeg {
		private ArrayList<Location> trackPoints;
		
		public TrackSeg(String xml) {
			trackPoints = new ArrayList<Location>();
			parseXml(xml);
		}
		
		public TrackSeg() {
			trackPoints = new ArrayList<Location>();
		}
		
		public void addPoint(Location location) {
			location.setTime(System.currentTimeMillis());
			Log.d("Track point", "Time: "+Long.toString(location.getTime()));
			trackPoints.add(location);
			Log.d("Track point", "Time: "+Long.toString(trackPoints.get(trackPoints.size()-1).getTime()));
		}
		
		public ArrayList<Location> getPoints() {
			return trackPoints;
		}
		
		public Location getLastPoint() {
			return trackPoints.get(trackPoints.size()-1);
		}
		
		public String getXml() {
			String xml = "";
			for(Location loc : trackPoints) {
				xml += "<trkpt lat=\""+loc.getLatitude()+"\" lon=\""+loc.getLongitude()+"\"><ele>"
			+loc.getAltitude()+"</ele><time>"+longToGPX(loc.getTime())+"</time></trkpt>";
			}
			return xml;
		}
		
		private void parseXml(String xml) {
			boolean isFirst = true;
			for(String trackPointString : xml.split("<trkpt lat=\"")) {
				if(isFirst) {
					isFirst = false;
					continue;
				}
				Location point = new Location("xml");
				
				String[] split = trackPointString.split("\" lon=\"");
				point.setLatitude(Double.parseDouble(split[0]));
				Log.d("Track", "Latitude: "+ split[0]);
				//This line makes me uncomfortable...
				split = split[1].split("\">");
				point.setLongitude(Double.parseDouble(split[0]));
				Log.d("Track", "Longitude: "+ split[0]);
				split = split[1].split("<ele>");
				split = split[1].split("</ele>");
				point.setAltitude(Double.parseDouble(split[0]));
				Log.d("Track", "Altitude: "+ split[0]);
				split = split[1].split("<time>");
				split = split[1].split("Z</time>");
				point.setTime(gpxToLong(split[0]));
				Log.d("Track", "Time: "+ split[0]);
				Log.d("Track", "Time: "+ point.getTime());
				trackPoints.add(point);
			}
		}
		
		private String longToGPX(long millis) {
			return (String) DateFormat.format("yyyy-MM-ddThh:mm:ssZ", new Date(millis));
			
		}
		
		private Long gpxToLong(String gpx) {
			String[] dateTime = gpx.split("T");
			String[] date = dateTime[0].split("-");
			String[] time = dateTime[1].split(":");
			Calendar c = Calendar.getInstance();
			c.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]), 
					Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));
			return c.getTimeInMillis();
			
		}
	}
}





