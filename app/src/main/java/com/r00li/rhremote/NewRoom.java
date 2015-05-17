package com.r00li.rhremote;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


public class NewRoom extends ActionBarActivity {
    String[] strings = {"Soba", "Kopalnica", "Kuhinja"};
    int arr_images[] = {R.drawable.ic_bed, R.drawable.ic_shower, R.drawable.ic_bulb_vec};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String[] strings = {"Soba", "Kopalnica", "Kuhinja"};
        int arr_images[] = {R.drawable.ic_bed, R.drawable.ic_shower, R.drawable.ic_bulb_vec};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_room);
        Spinner mySpinner = (Spinner) findViewById(R.id.spinner);
        mySpinner.setAdapter(new MyAdapter(NewRoom.this, R.layout.row, strings));

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
            label.setText(strings[position]);


            ImageView icon = (ImageView) row.findViewById(R.id.image);
            icon.setImageResource(arr_images[position]);

            return row;
        }
    }
}