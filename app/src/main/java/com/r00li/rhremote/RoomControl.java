package com.r00li.rhremote;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.lukedeighton.wheelview.adapter.*;
import com.lukedeighton.wheelview.*;

public class RoomControl extends AppCompatActivity {


    private static final int ITEM_COUNT = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_control);

        WheelView wheelView = (WheelView) findViewById(R.id.wheelview);

        HashMap<String, Integer> map = new HashMap<String,Integer>();
        map.put("Bla", 10);
        map.put("Trala", 0);
        List<Map.Entry<String, Integer>> materialColors = new ArrayList<Map.Entry<String, Integer>>();
        for(Map.Entry<String, Integer> entry : map.entrySet()) {
            materialColors.add(entry);
        }

        //create data for the adapter
        List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(ITEM_COUNT);
        for(int i = 0; i < ITEM_COUNT; i++) {
            entries.add(materialColors.get(0));
        }

        //populate the adapter, that knows how to draw each item (as you would do with a ListAdapter)
        wheelView.setAdapter(new MaterialColorAdapter(entries, this));

        //a listener for receiving a callback for when the item closest to the selection angle changes
        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectListener() {
            @Override
            public void onWheelItemSelected(WheelView parent, int position) {
                //get the item at this position
                Map.Entry<String, Integer> selectedEntry = ((MaterialColorAdapter) parent.getAdapter()).getItem(position);
                parent.setSelectionColor(getContrastColor(selectedEntry));
                //randomText.setText("izbran " + position);
            }
        });

        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @Override
            public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {
                String msg = String.valueOf(position) + " " + isSelected;
            }
        });

        //initialise the selection drawable with the first contrast color
        wheelView.setSelectionColor(getContrastColor(entries.get(0)));


        // ROOM ADAPTER
        RoomControlAdapter mAdapter = new RoomControlAdapter(this);
        for (int i = 1; i < 10; i++) {
            mAdapter.addItem("item " + i);
        }
        ListView list = (ListView) findViewById(R.id.listview);
        list.setAdapter(mAdapter);
    }

    //get the materials darker contrast
    private int getContrastColor(Map.Entry<String, Integer> entry) {
        return Color.parseColor("#0284f0");
    }

    static class MaterialColorAdapter extends WheelArrayAdapter<Map.Entry<String, Integer>> {

        Context context;

        MaterialColorAdapter(List<Map.Entry<String, Integer>> entries, Context context) {
            super(entries);
            this.context = context;
        }

        @Override
        public Drawable getDrawable(int position) {
            TextDrawable bottomLayer;
            if (position == 0)
                bottomLayer = new TextDrawable("" + position, context.getResources().getDrawable(R.drawable.ic_shower), context);
            else
                bottomLayer = new TextDrawable("" + position, context.getResources().getDrawable(R.drawable.ic_bed), context);
            return bottomLayer;
        }

        private Drawable createOvalDrawable(int color) {
            ShapeDrawable shapeDrawable = new ShapeDrawable(new OvalShape());
            shapeDrawable.getPaint().setColor(color);
            return shapeDrawable;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_room_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent i = new Intent(this, SettingsActivity.class);
            startActivityForResult(i, 1);

        }

        return super.onOptionsItemSelected(item);
    }

}

class RoomControlAdapter extends BaseAdapter {
    private final Activity context;
    private ArrayList mData = new ArrayList();
    private LayoutInflater mInflater;

    static class ViewHolder {
        public TextView text;
        public ImageView image;
    }

    public RoomControlAdapter(Activity context) {
        this.context = context;
        mInflater = context.getLayoutInflater();
    }

    public void addItem(final String item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (position > 3)? 1 : 0;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String getItem(int position) {
        return mData.get(position).toString();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        int type = getItemViewType(position);
        if (type == 0) {
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.room_light_cell_layout, parent, false);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.textView);
                viewHolder.image = (ImageView) rowView
                        .findViewById(R.id.imageView);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            String s = mData.get(position).toString();
            holder.text.setText(s);
        }
        else
        {
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.room_blinds_cell_layout, parent, false);
                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.text = (TextView) rowView.findViewById(R.id.textView);
                viewHolder.image = (ImageView) rowView
                        .findViewById(R.id.imageView);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();
            String s = mData.get(position).toString();
            holder.text.setText(s);
        }

        return rowView;
    }
}