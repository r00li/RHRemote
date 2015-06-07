package com.r00li.rhremote;

import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
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
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
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
    private ListView roomControlListView;

    private ProgressBar actionbarProgressBar;
    private TextView actionbarTitle;
    private TextView actionbarSubtitle;
    private ImageView actionBarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_room_control);

        // Initialize the managers
        RoomManager.context = this;
        RoomManager.eventListener = this;
        NotificationManager.context = this;

        // Customize the action bar
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.actionbar_custom, null);
        getSupportActionBar().setCustomView(mCustomView);

        actionbarProgressBar = (ProgressBar) mCustomView.findViewById(R.id.actionbarProgress);
        actionbarSubtitle = (TextView) mCustomView.findViewById(R.id.actionbarSubtitle);
        actionbarTitle = (TextView) mCustomView.findViewById(R.id.actionbarTitle);
        actionBarImageView = (ImageView) mCustomView.findViewById(R.id.actionbarIcon);

        actionbarProgressBar.setVisibility(View.GONE);

        // Setup the wheel selection menu
        wheelView = (WheelView) findViewById(R.id.wheelview);
        roomScrollerAdapter = new RoomScrollerAdapter(RoomManager.getRoomList(), this);
        wheelView.setAdapter(roomScrollerAdapter);
        wheelView.setSelectionColor(Color.parseColor("#0284f0"));

        // Callback for Wheel selector
        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectListener() {
            @Override
            public void onWheelItemSelected(WheelView parent, int position) {
                Log.d("Selection changed", "New position selected: " + position);
                if (RoomManager.getRoomList().size() == 0) {
                    return;
                }

                RoomManager.setCurrentRoom(RoomManager.getRoomList().get(position));
                RoomManager.updateRoomData(RoomManager.getRoomList().get(position));
                roomControlAdapter.setRoom(RoomManager.getRoomList().get(position));
                roomControlAdapter.notifyDataSetInvalidated();
                if (roomControlListView != null) {
                    roomControlListView.startLayoutAnimation();
                }

                actionbarSubtitle.setText("Room: " + RoomManager.getRoomList().get(position).name);
            }
        });

        // Room control adapter
        roomControlAdapter = new RoomControlAdapter(this);
        Room currentRoom = (RoomManager.getCurrentRoom() != null)? RoomManager.getCurrentRoom() : (RoomManager.getRoomList().size() > 0) ? RoomManager.getRoomList().get(0) : null;

        roomControlAdapter.setRoom(currentRoom);
        roomControlListView = (ListView) findViewById(R.id.listview);
        roomControlListView.setAdapter(roomControlAdapter);

        TextView emptyRow = (TextView) findViewById(R.id.emptyView);
        roomControlListView.setEmptyView(emptyRow);

        // Update data for first room when loading is done
        if (roomControlAdapter.getRoom() != null) {
            RoomManager.updateRoomData(roomControlAdapter.getRoom());
            actionbarSubtitle.setText("Room: " + roomControlAdapter.getRoom().name);
        }

        roomControlListView.setLayoutAnimation(getListViewAnimationController());

        // Scroll wheelView to position
        if (currentRoom != null) {
            wheelView.setPosition(RoomManager.getRoomList().indexOf(currentRoom));
        }

        // Create Notification service (shows persistent notification)
        RoomManager.setCurrentRoom(currentRoom); // Sets the current room - used by persistent notification
        Intent deleteIntent = new Intent(this, NotificationService.class);
        startService(deleteIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        RoomManager.saveRoomList();
    }

    public void roomUpdateStarted(Room r) {
        actionbarProgressBar.setVisibility(View.VISIBLE);
        actionBarImageView.setVisibility(View.GONE);
    }

    public void roomUpdateFailed(Room r) {
        actionbarProgressBar.setVisibility(View.GONE);
        actionBarImageView.setVisibility(View.VISIBLE);
    }

    public void roomUpdateComplete(Room r) {
        actionbarProgressBar.setVisibility(View.GONE);
        actionBarImageView.setVisibility(View.VISIBLE);

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
            actionbarSubtitle.setText("Room: " + roomControlAdapter.getRoom().name);
        }
        else {
            roomControlAdapter.setRoom(null);
            actionbarSubtitle.setText("");
        }

        Runnable run = new Runnable(){
            public void run(){
                roomControlAdapter.notifyDataSetChanged();
            }
        };
        this.runOnUiThread(run);
    }

    public void refreshIconClicked(MenuItem item) {
        RoomManager.updateRoomData(roomControlAdapter.getRoom());
    }

    public LayoutAnimationController getListViewAnimationController() {

        // Set up listview animation controller
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(300);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(300);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.15f);

        return controller;
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
            if (rooms.get(position).icon == -1)
                bottomLayer = new TextDrawable("" + position, context.getResources().getDrawable(R.drawable.ic_bed), context, rooms.get(position).color);
            else
                bottomLayer = new TextDrawable("" + position, context.getResources().getDrawable(rooms.get(position).icon), context, rooms.get(position).color);
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
    private static final int kTYPE_INFO_CELL = 2;

    static class ViewHolder {
        public TextView name;
        public TextView subtitle;
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
    public Room getRoom() {
        return room;
    }

    @Override
    public int getItemViewType(int position) {

        int lightCells = (room != null && room.lights != null) ? room.lights.size() : 0;
        int blindCells = (room != null && room.blinds != null) ? room.blinds.size() : 0;

        if (position < lightCells) {
            return kTYPE_LIGHT_CELL;
        }
        else if (position >= lightCells && position < lightCells + blindCells) {
            return kTYPE_BLIND_CELL;
        }
        else {
            return kTYPE_INFO_CELL;
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

        // Other info cell
        typeCount++;

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

        if (room != null) {
            // Other info cell
            itemCount++;
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
            if (rowView == null || rowView.getId() != R.id.light_cell) {
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
            holder.lightSwitch.setOnCheckedChangeListener(null);
            holder.lightSwitch.setChecked(room.lights.get(position).status != 0);

            // Action listeners
            final int innerIndex = position;
            /*holder.lightSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SwitchCompat buttonView = (SwitchCompat) v;
                    buttonView.setChecked(!buttonView.isChecked()); // The actual switch state should only change when the api confirms the action
                    RoomManager.modifyLightStatus(room, room.lights.get(innerIndex).id, (!buttonView.isChecked() == false) ? 0 : 1);
                }
            });*/

            holder.lightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonView.setOnCheckedChangeListener(null);
                    buttonView.setChecked(!isChecked);
                    buttonView.setOnCheckedChangeListener(this);
                    RoomManager.modifyLightStatus(room, room.lights.get(innerIndex).id, (isChecked == false) ? 0 : 1, false);
                }
            });
        }
        else if (type == kTYPE_BLIND_CELL)
        {
            if (rowView == null || rowView.getId() != R.id.blinds_cell) {
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
                    RoomManager.modifyBlindStatus(room, room.blinds.get(realIndex).id, room.blinds.get(realIndex).status - 1, false);
                }
            });

            holder.blindButtonPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RoomManager.modifyBlindStatus(room, room.blinds.get(realIndex).id, room.blinds.get(realIndex).status + 1, false);
                }
            });
        }
        else {
            if (rowView == null || rowView.getId() != R.id.info_cell) {
                LayoutInflater inflater = context.getLayoutInflater();
                rowView = inflater.inflate(R.layout.room_info_cell_layout, parent, false);

                // configure view holder
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.name = (TextView) rowView.findViewById(R.id.textView);
                viewHolder.subtitle = (TextView) rowView.findViewById(R.id.infoTextView);
                viewHolder.image = (ImageView) rowView.findViewById(R.id.imageView);
                rowView.setTag(viewHolder);
            }

            // fill data
            ViewHolder holder = (ViewHolder) rowView.getTag();

            holder.name.setText("Temperature: " + Math.round(room.temperature) + "°C");

            if (room.lastUpdate != null) {
                DateFormat formatter = DateFormat.getTimeInstance(DateFormat.MEDIUM);
                holder.subtitle.setText("Last updated: " + formatter.format(room.lastUpdate));
            }
            else {
                holder.subtitle.setText("Last updated: Never");
            }
        }

        return rowView;
    }
}