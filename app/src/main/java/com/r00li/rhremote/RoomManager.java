package com.r00li.rhremote;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by roli on 16/05/15.
 */

interface RoomManagerListener {
    public void roomUpdateComplete(Room r);
}

public class RoomManager {

    private static ArrayList<Room> rooms;
    public static Context context;
    public static RoomManagerListener eventListener;

    public static ArrayList<Room> getRoomList() {
        if (rooms == null) {
            rooms = new ArrayList<>();

            Room r1 = new Room();
            r1.name = "Test room";

            rooms.add(r1);
        }

        return rooms;
    }

    public static void updateRoomData(final Room room) {

        String url = "http://192.168.1.122:8080/user/user/api";

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
                        // TODO Auto-generated method stub

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

                JSONObject jsonBlind = lightArray.getJSONObject(i);
                blind.id = jsonBlind.getInt("id");
                blind.status = jsonBlind.getInt("status");
                blind.name = jsonBlind.getString("name");

                room.blinds.add(blind);
            }
            Log.d("Blinds parsed", room.blinds.toString());

            room.lastUpdate = new Date();

            Log.d("Parsing complete", "Rom parsing success." );

            if (eventListener != null) {
                eventListener.roomUpdateComplete(room);
            }
        }
        catch (JSONException e) {
            Log.e("JSON err", "Error parsing JSON: " + e.toString());
        }
    }

}
