package com.r00li.rhremote;

import android.content.Intent;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;


public class SettingsActivity extends PreferenceActivity  {
    // cache rooms to display them
    private  ArrayList<Room> rooms;
    // cache the Preference we create in this list so we can remove and add them later
    private List<Preference> mPreferenceList = new ArrayList<>();
    private static PreferenceScreen mRoot;
    private static PreferenceCategory preferenceCategoryRooms;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
            mRoot = getPreferenceManager().createPreferenceScreen(this);
            setGlobalSettings();
        getPreferencesFromArray();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferencesFromArray(); //refresh rooms category
    }

    private void setGlobalSettings()
    {
        PreferenceCategory preferenceCategorySettings = new PreferenceCategory(this);
        preferenceCategorySettings.setKey("settings");
        preferenceCategorySettings.setTitle(getString(R.string.settingsSettings));
        mRoot.addPreference(preferenceCategorySettings);

        EditTextPreference ssid=new EditTextPreference(this);
        ssid.setKey("SSID");
        ssid.setTitle(getString(R.string.settingsSSID));
        ssid.setSummary(getString(R.string.settingsSSIDSummary));
        ssid.setDefaultValue("");
        preferenceCategorySettings.addPreference(ssid);

        Preference nRoom =new Preference(this);
        nRoom.setKey("newRoom");
        nRoom.setTitle(getString(R.string.settingsNewRoom));
        nRoom.setSummary(getString(R.string.settingsNewRoomSummary));
        preferenceCategorySettings.addPreference(nRoom);
        Intent SetNewRoom= new Intent();
        SetNewRoom.setAction("newRoom");
        SetNewRoom.putExtra("room", -1);
        nRoom.setIntent(SetNewRoom);

        Preference about=new Preference(this);
        about.setKey("about");
        about.setTitle(getString(R.string.settingsAbout));
        about.setSummary(getString(R.string.settingsAboutSummary));
        preferenceCategorySettings.addPreference(about);
        Intent aboutS= new Intent();
        aboutS.setAction("about");
        about.setIntent(aboutS);

        preferenceCategoryRooms = new PreferenceCategory(this);
        preferenceCategoryRooms.setKey("rooms");
        preferenceCategoryRooms.setTitle(getString(R.string.settingsRooms));
        mRoot.addPreference(preferenceCategoryRooms);
    }
    private void getPreferencesFromArray() {
        int i=0;
        preferenceCategoryRooms.removeAll();
        mPreferenceList.clear();
        rooms=RoomManager.getRoomList();
        for(Room room :rooms)
        {
            Preference tRoom =new Preference(this);
            tRoom.setKey("room" + Integer.toString(i));
            tRoom.setTitle(room.name);
            tRoom.setSummary(getString(R.string.settingsRoomSettings));
            preferenceCategoryRooms.addPreference(tRoom);
            Intent SetNewRoom= new Intent();
            SetNewRoom.setAction("newRoom");
            SetNewRoom.putExtra("room", i);
            tRoom.setIntent(SetNewRoom);
            mPreferenceList.add(tRoom);
            i++;
        }
        setPreferenceScreen(mRoot);
    }

}