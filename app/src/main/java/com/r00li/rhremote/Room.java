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
    public String outisdeURL;
    public String username;
    public String password;

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
    }
}
