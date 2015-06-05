package com.r00li.rhremote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by roli on 29/04/15.
 */
public class Room implements Serializable {

    // General settings
    public String name;
    public String localURL;
    public String localPort;
    public String outsideURL;
    public String outsidePort;
    public String username;
    public String password;
    public int icon;
    public int color;

    // API objects
    public ArrayList<Light> lights;
    public ArrayList<Blind> blinds;
    public double temperature;
    public String time;
    public int api_ver;
    public String roomTime;

    // Other
    public Date lastUpdate;

    Room() {
        this.lights = new ArrayList<>();
        this.blinds = new ArrayList<>();

        this.name = RoomManager.context.getString(R.string.roomName);
        this.username = RoomManager.context.getString(R.string.roomUsername);
        this.password = RoomManager.context.getString(R.string.roomPassword);
        this.localURL = RoomManager.context.getString(R.string.roomLocalUrl);
        this.localPort = RoomManager.context.getString(R.string.roomLocalPort);
        this.outsideURL = RoomManager.context.getString(R.string.roomOutsideURL);
        this.outsidePort = RoomManager.context.getString(R.string.roomOutsidePort);
        this.icon = -1;
        this.color = -1;
    }
}
