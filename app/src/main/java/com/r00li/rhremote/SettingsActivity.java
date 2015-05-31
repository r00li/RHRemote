package com.r00li.rhremote;

import android.content.Intent;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends PreferenceActivity  {
    // cache rooms to display them
    private  ArrayList<Room> rooms;
    private static int firsttime=0;
    // cache the Preference we create in this list so we can remove and add them later
    private List<Preference> mPreferenceList = new ArrayList<>();
    private static PreferenceScreen mRoot;
    private static PreferenceCategory preferenceCategoryRooms;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        if(firsttime==0) {
            firsttime=1;
            mRoot = getPreferenceManager().createPreferenceScreen(this);
            setGlobalSettings();

        }
        getPreferencesFromArray();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        getPreferencesFromArray();
        // Get the Camera instance as the activity achieves full user focus

    }

    private void setGlobalSettings()
    {
        PreferenceCategory preferenceCategorySettings = new PreferenceCategory(this);
        preferenceCategorySettings.setKey("Settings");
        preferenceCategorySettings.setTitle("Settings");
        mRoot.addPreference(preferenceCategorySettings);

        EditTextPreference SSID=new EditTextPreference(this);
        SSID.setKey("SSID");
        SSID.setTitle("SSID");
        SSID.setSummary("SSID of the network where your rooms are connected.");
        SSID.setDefaultValue("");
        preferenceCategorySettings.addPreference(SSID);

        Preference NRoom=new Preference(this);
        NRoom.setKey("NewRoom");
        NRoom.setTitle("Add room");
        NRoom.setSummary("Add new room");
        preferenceCategorySettings.addPreference(NRoom);
        Intent SetNewRoom= new Intent();
        SetNewRoom.setAction("novasoba");
        SetNewRoom.putExtra("soba", -1);
        NRoom.setIntent(SetNewRoom);

        Preference about=new Preference(this);
        about.setKey("about");
        about.setTitle("About");
        about.setSummary("about");
        preferenceCategorySettings.addPreference(about);
        Intent aboutS= new Intent();
        aboutS.setAction("about");
        about.setIntent(aboutS);

        preferenceCategoryRooms = new PreferenceCategory(this);
        preferenceCategoryRooms.setKey("Rooms");
        preferenceCategoryRooms.setTitle("Rooms");
        mRoot.addPreference(preferenceCategoryRooms);
    }
    private void getPreferencesFromArray() {
        int i=0;
        preferenceCategoryRooms.removeAll();
        mPreferenceList.clear();
        rooms=RoomManager.getRoomList();
        for(Room room :rooms)
        {
            Preference TRoom=new Preference(this);
            TRoom.setKey("NewRoom");
            TRoom.setTitle(room.name);
            TRoom.setSummary("Room settings");
            preferenceCategoryRooms.addPreference(TRoom);
            Intent SetNewRoom= new Intent();
            SetNewRoom.setAction("novasoba");
            SetNewRoom.putExtra("soba", i);
            TRoom.setIntent(SetNewRoom);
            mPreferenceList.add(TRoom);
            i++;
        }
        setPreferenceScreen(mRoot);
    }

}