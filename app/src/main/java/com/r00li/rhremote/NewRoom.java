package com.r00li.rhremote;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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


public class NewRoom extends ActionBarActivity {
    String[] roomnames = {"Room", "Bathroom", "Kitchen"};
    int arr_images[] = {R.drawable.ic_bed, R.drawable.ic_shower, R.drawable.ic_bulb_vec};
    Room room;
    TextView roomname;
    TextView usrname;
    TextView password;
    TextView locIP;
    TextView locPort;
    TextView intIP;
    Button createroom;
    Button deleteroom;
    Spinner icon;
    int roomnumber;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);
        Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
        mySpinner.setAdapter(new MyAdapter(NewRoom.this, R.layout.row, roomnames));
        Intent intent = getIntent();
        roomnumber = intent.getIntExtra("soba",0);
        roomname = (TextView) findViewById(R.id.RoomName);
        usrname = (TextView) findViewById(R.id.UserName);
        password=(TextView)findViewById(R.id.Password);
        locIP=(TextView) findViewById(R.id.LklIP);
        locPort=(TextView)findViewById(R.id.LklPort);
        intIP=(TextView)findViewById(R.id.IntIP);
        icon=(Spinner)findViewById(R.id.spinner);
        createroom=(Button)findViewById(R.id.createroom);
        deleteroom=(Button)findViewById(R.id.deleteroom);
        if (roomnumber==-1)
        {
            room=new Room();
            newroom();
        }
        else
        {
            room = RoomManager.getRoomList().get(roomnumber);
            updateroom();
        }

    }
    private void newroom()
    {
        LinearLayout.LayoutParams createPar = new LinearLayout.LayoutParams(createroom.getLayoutParams());
        LinearLayout.LayoutParams deletePar = new LinearLayout.LayoutParams(deleteroom.getLayoutParams());
        createPar.weight = 0;
        deletePar.weight=80;
        roomname.setText("New room name");
        usrname.setText("MyUserName");
        password.setText("password");
        locIP.setText("0.0.0.0");
        locPort.setText("1234");
        intIP.setText("0.0.0.0");
        icon.setSelection(0);
        createroom.setLayoutParams(createPar);
        createroom.setText("Create room");
        deleteroom.setLayoutParams(deletePar);
    }

    private void updateroom()
    {
        LinearLayout.LayoutParams createPar = new LinearLayout.LayoutParams(createroom.getLayoutParams());
        LinearLayout.LayoutParams deletePar = new LinearLayout.LayoutParams(deleteroom.getLayoutParams());
        createPar.weight = 30;
        deletePar.weight=80;
        roomname.setText(room.name);
        usrname.setText(room.username);
        password.setText(room.password);
        locIP.setText(room.localURL);
        locPort.setText("1234");
        intIP.setText(room.outside);
        icon.setSelection(room.icon);
        createroom.setLayoutParams(createPar);
        createroom.setText("Update room");
        deleteroom.setLayoutParams(deletePar);
    }


    public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.createroom:
                room.name=(String)roomname.getText().toString();
                room.username=(String)usrname.getText().toString();
                room.password=(String)password.getText().toString();
                room.localURL=(String)locIP.getText().toString();
                room.outside =(String)intIP.getText().toString();
                //room.icon=(int)icon.getSelectedItemId();
                RoomManager.ModifyRoomData(roomnumber, room);
            case R.id.deleteroom:
                RoomManager.DeleteRoom(roomnumber);
                break;

        }
        this.finish();
        return;
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

            TextView label = (TextView) row.findViewById(R.id.ImgName);
            label.setText(roomnames[position]);


            ImageView icon = (ImageView) row.findViewById(R.id.image);
            icon.setImageResource(arr_images[position]);

            return row;
        }
    }
}