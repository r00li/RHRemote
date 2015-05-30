package com.r00li.rhremote;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
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
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import com.lukedeighton.wheelview.adapter.*;
import com.lukedeighton.wheelview.*;

public class RoomControl extends AppCompatActivity implements RoomManagerListener {

    private RoomControlAdapter roomControlAdapter;
    private RoomScrollerAdapter roomScrollerAdapter;
    private WheelView wheelView;

    private ProgressBar actionbarProgressBar;
    private TextView actionbarTitle;
    private TextView actionbarSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_room_control);
        RoomManager.context = this;
        RoomManager.eventListener = this;

        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_custom, null);
        getSupportActionBar().setCustomView(mCustomView);

        actionbarProgressBar = (ProgressBar) mCustomView.findViewById(R.id.actionbarProgress);
        actionbarSubtitle = (TextView) mCustomView.findViewById(R.id.actionbarSubtitle);
        actionbarTitle = (TextView) mCustomView.findViewById(R.id.actionbarTitle);

        actionbarProgressBar.setVisibility(View.GONE);

        wheelView = (WheelView) findViewById(R.id.wheelview);

        //populate the adapter, that knows how to draw each item (as you would do with a ListAdapter)
        roomScrollerAdapter = new RoomScrollerAdapter(RoomManager.getRoomList(), this);
        wheelView.setAdapter(roomScrollerAdapter);

        //a listener for receiving a callback for when the item closest to the selection angle changes
        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectListener() {
            @Override
            public void onWheelItemSelected(WheelView parent, int position) {
                Log.d("Selection changed", "New position selected: " + position);
                RoomManager.updateRoomData(RoomManager.getRoomList().get(position));
                roomControlAdapter.setRoom(RoomManager.getRoomList().get(position));
                roomControlAdapter.notifyDataSetChanged();

                actionbarSubtitle.setText("Room: " + RoomManager.getRoomList().get(position).name);
            }
        });

        wheelView.setOnWheelItemClickListener(new WheelView.OnWheelItemClickListener() {
            @Override
            public void onWheelItemClick(WheelView parent, int position, boolean isSelected) {
                String msg = String.valueOf(position) + " " + isSelected;
            }
        });

        //initialise the selection drawable with the first contrast color
        wheelView.setSelectionColor(Color.parseColor("#0284f0"));


        // ROOM ADAPTER
        roomControlAdapter = new RoomControlAdapter(this);
        Room currentRoom = (RoomManager.getRoomList().size() > 0)? RoomManager.getRoomList().get(0) : null;
        roomControlAdapter.setRoom(currentRoom);
        ListView list = (ListView) findViewById(R.id.listview);
        list.setAdapter(roomControlAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        RoomManager.saveRoomList();
    }

    public void roomUpdateComplete(Room r) {
        Log.d("Room", "Just updated room data");
        //roomControlAdapter.setRoom(r);

        Runnable run = new Runnable(){
            public void run(){
                roomControlAdapter.notifyDataSetChanged();
            }
        };
        this.runOnUiThread(run);
    }

    public void numberOfRoomsChanged() {
        roomScrollerAdapter.setRooms(RoomManager.getRoomList());
        wheelView.setAdapter(roomScrollerAdapter);

        if (RoomManager.getRoomList().size() > 0) {
            roomControlAdapter.setRoom(RoomManager.getRoomList().get(0));
        }
        else {
            roomControlAdapter.setRoom(null);
        }

        Runnable run = new Runnable(){
            public void run(){
                roomControlAdapter.notifyDataSetChanged();
            }
        };
        this.runOnUiThread(run);
    }

    static class RoomScrollerAdapter implements WheelAdapter {

        Context context;
        private ArrayList<Room> rooms;

        public void setRooms(ArrayList<Room> rooms) {
            this.rooms = rooms;
        }

        RoomScrollerAdapter(ArrayList<Room> entries, Context context) {
            this.rooms = entries;
            this.context = context;
        }

        @Override
        public int getCount() {
            if (rooms == null) {
                return 0;
            }
            return rooms.size();
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
    private Room room;
    private LayoutInflater mInflater;

    private static final int kTYPE_LIGHT_CELL = 0;
    private static final int kTYPE_BLIND_CELL = 1;

    static class ViewHolder {
        public TextView name;
        public ImageView image;
        public SwitchCompat lightSwitch;
        public View blindIndicator0, blindIndicator1, blindIndicator2;
        public AppCompatButton blindButtonPlus;
        public AppCompatButton blindButtonMinus;

    }

    public RoomControlAdapter(Activity context) {
        this.context = context;
        mInflater = context.getLayoutInflater();
    }

    public void setRoom(Room r) {
        room = r;
    }

    @Override
    public int getItemViewType(int position) {
        if (room != null && room.lights != null && position < room.lights.size()) {
            return kTYPE_LIGHT_CELL;
        }
        else {
            return kTYPE_BLIND_CELL;
        }
    }

    @Override
    public int getViewTypeCount() {
        int typeCount = 0;
        if (room != null && room.lights != null && room.lights.size() > 0) {
            typeCount++;
        }

        if (room != null && room.blinds != null && room.blinds.size() > 0) {
            typeCount++;
        }

        if (typeCount == 0) {
            return 1;
        }

        return typeCount;
    }

    @Override
    public int getCount() {
        int itemCount = 0;
        if (room != null && room.lights != null) {
            itemCount += room.lights.size();
        }

        if (room != null && room.blinds != null) {
            itemCount += room.blinds.size();
        }

        return itemCount;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String getItem(int position) {
        return "";
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        int type = getItemViewType(position);
        if (type == kTYPE_LIGHT_CELL) {
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.room_light_cell_layout, parent, false);

                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.name = (TextView) rowView.findViewById(R.id.textView);
                viewHolder.image = (ImageView) rowView.findViewById(R.id.imageView);
                viewHolder.lightSwitch = (SwitchCompat) rowView.findViewById(R.id.switch1);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();

            holder.name.setText(room.lights.get(position).name);
            holder.lightSwitch.setChecked(room.lights.get(position).status != 0);

            // Action listeners
            final int innerIndex = position;
            holder.lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    RoomManager.modifyLightStatus(room, room.lights.get(innerIndex).id, (isChecked == false)? 0 : 1);
                }
            });
        }
        else
        {
            if (rowView == null) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.room_blinds_cell_layout, parent, false);

                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.name = (TextView) rowView.findViewById(R.id.textView);
                viewHolder.image = (ImageView) rowView.findViewById(R.id.imageView);
                viewHolder.blindIndicator0 = (View)rowView.findViewById(R.id.blindIndicator0);
                viewHolder.blindIndicator1 = (View)rowView.findViewById(R.id.blindIndicator1);
                viewHolder.blindIndicator2 = (View)rowView.findViewById(R.id.blindIndicator2);
                viewHolder.blindButtonMinus = (AppCompatButton)rowView.findViewById(R.id.blindButtonMinus);
                viewHolder.blindButtonPlus = (AppCompatButton)rowView.findViewById(R.id.blindButtonPlus);
                rowView.setTag(viewHolder);
            }

            // fill data
            final int realIndex = position - room.lights.size();

            ViewHolder holder = (ViewHolder) rowView.getTag();
            holder.name.setText(room.blinds.get(realIndex).name);
            holder.blindIndicator2.setBackgroundColor((room.blinds.get(realIndex).status == 2)? 0xFFffc719 : 0x4Dffc719);
            holder.blindIndicator1.setBackgroundColor((room.blinds.get(realIndex).status == 1)? 0xFFffc719 : 0x4Dffc719);
            holder.blindIndicator0.setBackgroundColor((room.blinds.get(realIndex).status == 0)? 0xFFffc719 : 0x4Dffc719);

            if (room.blinds.get(realIndex).status == 2) {
                holder.blindButtonPlus.setEnabled(false);
            }
            else {
                holder.blindButtonPlus.setEnabled(true);
            }

            if (room.blinds.get(realIndex).status == 0) {
                holder.blindButtonMinus.setEnabled(false);
            }
            else {
                holder.blindButtonMinus.setEnabled(true);
            }

            // Action Listeners
            holder.blindButtonMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RoomManager.modifyBlindStatus(room, room.blinds.get(realIndex).id, room.blinds.get(realIndex).status - 1);
                }
            });

            holder.blindButtonPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RoomManager.modifyBlindStatus(room, room.blinds.get(realIndex).id, room.blinds.get(realIndex).status + 1);
                }
            });
        }

        return rowView;
    }
}