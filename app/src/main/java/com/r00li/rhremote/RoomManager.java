package com.r00li.rhremote;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
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
    public void roomUpdateComplete(Room r);
    public void numberOfRoomsChanged();
}

public class RoomManager {

    private static ArrayList<Room> rooms;
    public static Context context;
    public static RoomManagerListener eventListener;
    private static ProgressDialog progressDialog;

    public static ArrayList<Room> getRoomList() {

        if (rooms == null) {
            readRoomList();
        }

        if (rooms == null) {
            rooms = new ArrayList<>();

            Room r1 = new Room();

            rooms.add(r1);
        }

        return rooms;
    }

    public static boolean saveRoomList() {
        if (rooms == null || rooms.size() == 0) {
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

    public static void modifyLightStatus(Room room, int lightId, int newStatus) {
        progressDialog = ProgressDialog.show(context, "", "Please wait...");
        String url = formURLForAPIPath(room, "api/lght/" + lightId + "/" + ((newStatus == 0)? "off" : "on"));
        updateRoomData(room, url);
    }

    public static void modifyBlindStatus(Room room, int blindId, int newStatus) {
        progressDialog = ProgressDialog.show(context, "", "Please wait...");
        String url = formURLForAPIPath(room, "api/bld/" + blindId + "/" + newStatus);
        updateRoomData(room, url);
    }

    public static void updateRoomData(final Room room) {
        String url = formURLForAPIPath(room, "api");

        updateRoomData(room, url);
    }

    public static void updateRoomData(final Room room, String url) {

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

                        if (progressDialog != null) {
                            progressDialog.dismiss();
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
            Log.d("Lights parsed", room.lights.toString());

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
            Log.d("Blinds parsed", room.blinds.toString());

            room.lastUpdate = new Date();

            Log.d("Parsing complete", "Rom parsing success." );
            saveRoomList();

            if (eventListener != null) {
                eventListener.roomUpdateComplete(room);

                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        }
        catch (JSONException e) {
            Log.e("JSON err", "Error parsing JSON: " + e.toString());

            if (progressDialog != null) {
                progressDialog.dismiss();
            }
        }
    }

    public static void ModifyRoomData (int RoomNumber, Room RoomData)
    {
        if (RoomNumber==-1)
            getRoomList().add(RoomData);
        else
            getRoomList().set(RoomNumber, RoomData);
        numberOfRoomsChanged();
    }

    public static void DeleteRoom(int RoomNumber)
    {
        getRoomList().remove(RoomNumber);
        numberOfRoomsChanged();
    }

    public static String formURLForAPIPath(Room room, String path) {
        String url = "";
        try {
            // TODO: Uncomment port section
            URL addres = new URL("http", room.localURL/*, Integer.parseInt(room.localPort)*/, room.username + "/" + room.password + "/" + path);
            Log.d("url", addres.toString());
            url = addres.toString();
        }
        catch (MalformedURLException ex) {
            Log.e("url", "Invalid Url" + ex.getMessage());
        }

        return url;
    }

}
