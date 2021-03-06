package com.r00li.rhremote;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


public class NewRoom extends AppCompatActivity {

    private static String[] roomNames = {"Bedroom","Bathroom", "Kitchen", "Living room", "Work room", "Coat room", "Guest room", "Study room"};
    private static String[] roomColors = {"1", "2", "3", "4", "5", "6"};
    private static ArrayList<Integer> iconImages;
    private static ArrayList<Integer> iconColors;

    Room room;
    TextView roomName;
    TextView username;
    TextView password;
    TextView localIP;
    TextView localPort;
    TextView internetIP;
    TextView internetPort;
    Button roomCreate;
    Button roomDelete;
    Spinner spinnerIcon;
    Spinner spinnerColor;
    int roomNumber;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iconImages = new ArrayList<Integer>();
        iconImages.add(R.drawable.ic_bed);
        iconImages.add(R.drawable.ic_shower);
        iconImages.add(R.drawable.ic_table);
        iconImages.add(R.drawable.ic_couch);
        iconImages.add(R.drawable.ic_desk);
        iconImages.add(R.drawable.ic_coathanger);
        iconImages.add(R.drawable.ic_guestbedroom);
        iconImages.add(R.drawable.ic_lamp);

        iconColors=new ArrayList<Integer>();
        iconColors.add(0xfff86a69);
        iconColors.add(0xff56d882);
        iconColors.add(0xfff08e4d);
        iconColors.add(0xffecc961);
        iconColors.add(0xffa04e7e);
        iconColors.add(0xff22c7c1);

        setContentView(R.layout.activity_new_room);

        Intent intent = getIntent();
        roomNumber = intent.getIntExtra("room",-1);
        roomName = (TextView) findViewById(R.id.roomName);
        username = (TextView) findViewById(R.id.userName);
        password=(TextView)findViewById(R.id.password);
        localIP =(TextView) findViewById(R.id.localIP);
        localPort =(TextView)findViewById(R.id.localPort);
        internetIP =(TextView)findViewById(R.id.internetIP);
        internetPort =(TextView)findViewById(R.id.internetPort);
        spinnerIcon =(Spinner)findViewById(R.id.spinnerIcon);
        spinnerColor=(Spinner)findViewById(R.id.spinnerColor);
        roomCreate =(Button)findViewById(R.id.createRoom);
        roomDelete =(Button)findViewById(R.id.deleteRoom);
        spinnerIcon.setAdapter(new MyAdapterImages(NewRoom.this, R.layout.row, roomNames));
        spinnerColor.setAdapter(new MyAdapterColors(NewRoom.this, R.layout.row, roomColors));
        LinearLayout.LayoutParams createPar = new LinearLayout.LayoutParams(roomCreate.getLayoutParams());
        if (roomNumber ==-1)
        {
            room=new Room();
            createPar.weight = 0;
            roomCreate.setText(getString(R.string.createRoom));
        }
        else
        {
            room = RoomManager.getRoomList().get(roomNumber);
            createPar.weight = 35;
            roomCreate.setText(getString(R.string.updateRoom));
            roomDelete.setText(getString(R.string.deleteRoom));

            roomName.setText(room.name);
            username.setText(room.username);
            password.setText(room.password);
            localIP.setText(room.localURL);
            localPort.setText(room.localPort);
            internetIP.setText(room.outsideURL);
            spinnerIcon.setSelection(iconImages.indexOf(room.icon));
            spinnerColor.setSelection(iconColors.indexOf(room.color));
            internetPort.setText(room.outsidePort);
        }

        roomCreate.setLayoutParams(createPar);
    }


    public void createRoomClicked(View v) {
        if (!"".equals(roomName.getText().toString()))
        room.name = roomName.getText().toString();
        if (!"".equals(username.getText().toString()))
        room.username = username.getText().toString();
        if (!"".equals(password.getText().toString()))
        room.password = password.getText().toString();
        if (!"".equals(localIP.getText().toString()))
        room.localURL = localIP.getText().toString();
        if (!"".equals(localPort.getText().toString()))
        room.localPort = localPort.getText().toString();
        if (!"".equals(internetIP.getText().toString()))
        room.outsideURL = internetIP.getText().toString();
        if (!"".equals(internetPort.getText().toString()))
        room.outsidePort=internetPort.getText().toString();
        room.icon = iconImages.get(spinnerIcon.getSelectedItemPosition());
        room.color = iconColors.get(spinnerColor.getSelectedItemPosition());
        RoomManager.ModifyRoomData(roomNumber, room);
        this.finish();
    }

    public void deleteRoomClicked(View v) {
        RoomManager.DeleteRoom(roomNumber);
        this.finish();
    }

    public class MyAdapterImages extends ArrayAdapter<String>  {

        public MyAdapterImages(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);

            ImageView icon = (ImageView) row.findViewById(R.id.image);
            icon.setImageResource(iconImages.get(position));

            return row;
        }
    }

    public class MyAdapterColors extends ArrayAdapter<String>  {

        public MyAdapterColors(Context context, int textViewResourceId, String[] objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);

            ImageView icon = (ImageView) row.findViewById(R.id.image);
            //icon.setImageResource();
            icon.setImageDrawable(new ColorDrawable(iconColors.get(position)));

            return row;
        }
    }


}