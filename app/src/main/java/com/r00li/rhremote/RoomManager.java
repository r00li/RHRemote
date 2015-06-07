package com.r00li.rhremote;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by roli on 16/05/15.
 */

interface RoomManagerListener {
    public void roomUpdateStarted(Room r);
    public void roomUpdateFailed(Room r);
    public void roomUpdateComplete(Room r);
    public void numberOfRoomsChanged();
}

public class RoomManager {

    private static ArrayList<Room> rooms;
    public static Context context;
    public static RoomManagerListener eventListener;
    private static ProgressDialog progressDialog;
    private static Room currentRoom;

    public static Room getCurrentRoom() {
        return currentRoom;
    }

    public static void setCurrentRoom(Room currentRoom) {
        RoomManager.currentRoom = currentRoom;

        Log.d("Notification", "Sending room change broadcast");
        Intent localIntent = new Intent(NotificationService.ROOM_SELECTED);
        context.sendBroadcast(localIntent);
    }

    public static ArrayList<Room> getRoomList() {

        if (rooms == null) {
            readRoomList();
        }

        if (rooms == null) {
            rooms = new ArrayList<>();
        }

        return rooms;
    }

    public static boolean saveRoomList() {
        if (rooms == null) {
            Log.d("serialization", "No rooms to save");
            return false;
        }

        try {
            FileOutputStream outStream = context.openFileOutput("roomList", Context.MODE_PRIVATE);
            ObjectOutputStream objectOut = new ObjectOutputStream(outStream);
            objectOut.writeObject(rooms);
            objectOut.close();
            outStream.close();

            Log.d("serialization", "Roomlist saved");
        }
        catch (IOException ex) {
            Log.e("serialization", ex.getMessage());
            return false;
        }

        return true;
    }

    public static boolean readRoomList() {
        try {
            FileInputStream inStream = context.openFileInput("roomList");
            ObjectInputStream objectInStream = new ObjectInputStream(inStream);
            rooms = (ArrayList<Room>) objectInStream.readObject();

            Log.d("serialization", "Room list read from file");
        }
        catch (IOException ex) {
            Log.e("serialization", "serialization failed: " + ex.getMessage());
            rooms = null;

            return false;
        }
        catch (ClassNotFoundException ex) {
            Log.e("Serialization", "Serialization failed because class was not found: " + ex.getMessage());
            rooms = null;

            return false;
        }

        return true;
    }

    public static void numberOfRoomsChanged() {
        if (eventListener != null) {
            eventListener.numberOfRoomsChanged();
        }
    }

    public static void modifyLightStatus(Room room, int lightId, int newStatus, boolean background) {
        if (!background) {
            progressDialog = ProgressDialog.show(context, "", "Please wait...");
        }
        String url = formURLForAPIPath(room, "api/lght/" + lightId + "/" + ((newStatus == 0)? "off" : "on"));
        updateRoomData(room, url);
    }

    public static void modifyBlindStatus(Room room, int blindId, int newStatus, boolean background) {
        if (!background) {
            progressDialog = ProgressDialog.show(context, "", "Please wait...");
        }
        String url = formURLForAPIPath(room, "api/bld/" + blindId + "/" + newStatus);
        updateRoomData(room, url);
    }

    public static void updateRoomData(final Room room) {
        if (room == null) {
            return;
        }

        String url = formURLForAPIPath(room, "api");

        updateRoomData(room, url);
    }

    public static void updateRoomData(final Room room, String url) {

        if (url.length() < 1) {
            return;
        }

        if (eventListener != null) {
            eventListener.roomUpdateStarted(room);
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", "Response: " + response.toString());
                        parseResponseData(room, response);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error", error.toString());

                        if (error != null && error.networkResponse != null && error.networkResponse.statusCode == 403) {
                            NotificationManager.showAlertDialogMessage("Authentication error! Check your username and password!");
                        }
                        else {
                            NotificationManager.showToastMessage("We couldn't get the room data! Check your room settings.");
                        }

                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }

                        if (eventListener != null) {
                            eventListener.roomUpdateFailed(room);
                        }
                    }
                });

        NetworkHelper.getInstance(context).addToRequestQueue(jsObjRequest);
    }

    private static void parseResponseData(Room room, JSONObject response) {

        try {
            room.api_ver = response.getInt("api_ver");
            room.temperature = response.getDouble("temperature");
            room.roomTime = response.getString("time");

            JSONArray lightArray = response.getJSONArray("lights");
            room.lights.clear();
            for (int i=0; i < lightArray.length(); i++) {
                Light light = new Light();

                JSONObject jsonLight = lightArray.getJSONObject(i);
                light.id = jsonLight.getInt("id");
                light.status = jsonLight.getInt("status");
                light.name = jsonLight.getString("name");

                room.lights.add(light);
            }

            JSONArray blindArray = response.getJSONArray("blinds");
            room.blinds.clear();
            for (int i=0; i < blindArray.length(); i++) {
                Blind blind = new Blind();

                JSONObject jsonBlind = blindArray.getJSONObject(i);
                blind.id = jsonBlind.getInt("id");
                blind.status = jsonBlind.getInt("status");
                blind.name = jsonBlind.getString("name");

                room.blinds.add(blind);
            }

            room.lastUpdate = new Date();

            Log.d("Parsing complete", "Rom parsing success." );
            saveRoomList();

            // Update the notification
            Intent localIntent = new Intent(NotificationService.ROOM_UPDATED);
            context.sendBroadcast(localIntent);

            if (eventListener != null) {
                eventListener.roomUpdateComplete(room);

                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        }
        catch (JSONException e) {
            Log.e("JSON err", "Error parsing JSON: " + e.toString());
            NotificationManager.showToastMessage("Something went wrong! We couldn't read the room response!");

            if (progressDialog != null) {
                progressDialog.dismiss();
            }

            if (eventListener != null) {
                eventListener.roomUpdateFailed(room);
            }
        }
    }

    public static void ModifyRoomData (int roomNumber, Room roomData)
    {
        if (roomNumber==-1)
            getRoomList().add(roomData);
        else
            getRoomList().set(roomNumber, roomData);

        saveRoomList();
        numberOfRoomsChanged();
    }

    public static void DeleteRoom(int roomNumber)
    {
        getRoomList().remove(roomNumber);
        saveRoomList();
        numberOfRoomsChanged();
    }

    public static String formURLForAPIPath(Room room, String path) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String savedSSID = sharedPref.getString("SSID", "");

        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String currentSSID = wifiInfo.getSSID();
        if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")){
            // Android 4.2 and later puts extra " " around SSID name returned - remove them
            currentSSID = currentSSID.substring(1, currentSSID.length()-1);
        }

        String url = "";
        try {
            if (savedSSID.equals(currentSSID)) {
                // TODO: Uncomment port section
                URL address = new URL("http", room.localURL/*, Integer.parseInt(room.localPort)*/, room.username + "/" + room.password + "/" + path);
                url = address.toString();
            }
            else {
                Log.d("url", "Using external room URL (current SSID: " + currentSSID + ", saved SSID: " + savedSSID + ")");
                if (room.outsideURL.length() > 0 && room.outsidePort.length() > 0) {
                    URL address = new URL("http", room.outsideURL, Integer.parseInt(room.outsidePort), room.username + "/" + room.password + "/" + path);
                    url = address.toString();
                }
                else {
                    NotificationManager.showToastMessage("Error connecting! You are not in the same network as your room and you haven't provided an external URL!");
                }
            }
        }
        catch (MalformedURLException ex) {
            Log.e("url", "Invalid Url" + ex.getMessage());
            NotificationManager.showAlertDialogMessage("There was an error forming a valid connection URL. Check your room connection settings!");
        }

        return url;
    }

}
