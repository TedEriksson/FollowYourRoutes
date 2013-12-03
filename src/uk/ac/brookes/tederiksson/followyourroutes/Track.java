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
	private String userID, name, xml;
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
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"><brookesml><user-id>NOTSET";
		return xml;
	}
	
	public String getXml(String name) {
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"><brookesml><user-id>NOTSET</user-id><date>ISO 8601 format date</date><trk><name>"
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
	
	public static LatLng LocationToLatLng(Location loc) {
		return new LatLng(loc.getLatitude(), loc.getLongitude());
	}
	
	public long getRunTime() {
		return trackSegs.get(trackSegs.size()-1).getLastPoint().getTime() - getFirstLocation().getTime();
	}
	
	public Location getFirstLocation() {
		return trackSegs.get(0).getPoints().get(0);
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
			trackPoints.add(location);
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





