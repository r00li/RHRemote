package com.r00li.rhremote;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
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

    private static String[] roomNames = {"Bedroom", "Bathroom", "Kitchen", "Living room", "Work room", "Coat room", "Guest room", "Study room"};
    private static ArrayList<Integer> iconImages;

    Room room;
    TextView roomname;
    TextView usrname;
    TextView password;
    TextView locIP;
    TextView locPort;
    TextView intIP;
    TextView intPort;
    Button createroom;
    Button deleteroom;
    Spinner icon;
    int roomnumber;

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

        setContentView(R.layout.activity_new_room);
        Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
        mySpinner.setAdapter(new MyAdapter(NewRoom.this, R.layout.row, roomNames));
        Intent intent = getIntent();
        roomnumber = intent.getIntExtra("soba",0);
        roomname = (TextView) findViewById(R.id.RoomName);
        usrname = (TextView) findViewById(R.id.UserName);
        password=(TextView)findViewById(R.id.Password);
        locIP=(TextView) findViewById(R.id.LklIP);
        locPort=(TextView)findViewById(R.id.LklPort);
        intIP=(TextView)findViewById(R.id.IntIP);
        intPort=(TextView)findViewById(R.id.IntPort);
        icon=(Spinner)findViewById(R.id.spinner);
        createroom=(Button)findViewById(R.id.createroom);
        deleteroom=(Button)findViewById(R.id.deleteroom);
        LinearLayout.LayoutParams createPar = new LinearLayout.LayoutParams(createroom.getLayoutParams());
        if (roomnumber==-1)
        {
            room=new Room();
            createPar.weight = 0;
            createroom.setText("Create room");
        }
        else
        {
            room = RoomManager.getRoomList().get(roomnumber);
            createPar.weight = 30;
            createroom.setText("Update room");
        }

        roomname.setText(room.name);
        usrname.setText(room.username);
        password.setText(room.password);
        locIP.setText(room.localURL);
        locPort.setText(room.localPort);
        intIP.setText(room.outsideURL);
        icon.setSelection(iconImages.indexOf(room.icon));
        intPort.setText(room.outsidePort);
        createroom.setLayoutParams(createPar);
    }

    public void createRoomClicked(View v) {
        room.name = roomname.getText().toString();
        room.username = usrname.getText().toString();
        room.password = password.getText().toString();
        room.localURL = locIP.getText().toString();
        room.localPort = locPort.getText().toString();
        room.outsideURL = intIP.getText().toString();
        room.icon = iconImages.get(icon.getSelectedItemPosition());
        RoomManager.ModifyRoomData(roomnumber, room);

        this.finish();
    }

    public void deleteRoomClicked(View v) {
        RoomManager.DeleteRoom(roomnumber);

        this.finish();
    }

    public class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(Context context, int textViewResourceId, String[] objects) {
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
}