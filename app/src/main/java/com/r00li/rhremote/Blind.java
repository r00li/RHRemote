package com.r00li.rhremote;

import java.io.Serializable;

/**
 * Created by roli on 16/05/15.
 */
public class Blind implements Serializable {

    public int id;
    public String name;
    public int status;

    public String toString() {
        return id + " - " + name + " - " + status;
    }
}
