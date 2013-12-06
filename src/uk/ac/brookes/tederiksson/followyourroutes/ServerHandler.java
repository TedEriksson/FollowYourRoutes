package uk.ac.brookes.tederiksson.followyourroutes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

public class ServerHandler {
	private static ServerHandler serverHandler = null;
	private static final String ADDRESS = "161.73.245.41";
	private static final int PORT = 44365;
	
	protected ServerHandler() {}
	
	public static ServerHandler getInstance() {
		if(serverHandler == null) {
			serverHandler = new ServerHandler();
		}
		return serverHandler;
	}
	
	public static boolean uploadTrack(String xml) {
		getInstance();
		try {
			Socket socket = new Socket(ADDRESS, PORT);
			PrintStream out = new PrintStream(socket.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out.println("STORE " + xml + "");
			String returnedData = input.readLine();
			Log.d("uploadTrack", xml);
			socket.close();
			if(!returnedData.equals(" ")){
				return true;
			}
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("SERVER", "Can't connect");
			e.printStackTrace();
		}
		return false;
	}
	
	public static String searchDatabase(String userID) {
		getInstance();
		try {
			Socket socket = new Socket(ADDRESS, PORT);
			PrintStream out = new PrintStream(socket.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println("SEARCH " + userID + " ");
			String returnedData = input.readLine();
			socket.close();
			return returnedData;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("SERVER", "Can't connect");
			e.printStackTrace();
		}
		return " ";
	}
	
	public static String getXMLByRunID(String runID) {
		Socket socket;
		try {
			socket = new Socket(ADDRESS, PORT);
			PrintStream out = new PrintStream(socket.getOutputStream(), true);
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			out.println("RETRIEVE " + runID + " ");
			String returnedData = input.readLine();
			socket.close();
			return returnedData;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
