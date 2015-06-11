package com.r00li.rhremote;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Created by roli on 05/06/15.
 */
public class NotificationService extends IntentService {

    private static final int notificationId = 6599231;

    public static final String ROOM_SELECTED = "com.r00li.rhremote.ROOM_SELECTED";
    public static final String ROOM_UPDATED = "com.r00li.rhremote.ROOM_UPDATED";
    public static final String BUTTON_PRESSED = "com.r00li.rhremote.BUTTON_PRESSED";
    public static final String BUTTON_PRESSED_0 = "com.r00li.rhremote.BUTTON_PRESSED_0";
    public static final String BUTTON_PRESSED_1 = "com.r00li.rhremote.BUTTON_PRESSED_1";
    public static final String BUTTON_PRESSED_2 = "com.r00li.rhremote.BUTTON_PRESSED_2";
    public static final String BUTTON_PRESSED_3 = "com.r00li.rhremote.BUTTON_PRESSED_3";
    public static final String BUTTON_PRESSED_4 = "com.r00li.rhremote.BUTTON_PRESSED_4";
    public static final String BUTTON_PRESSED_5 = "com.r00li.rhremote.BUTTON_PRESSED_5";

    private NotificationCompat.Builder mBuilder;
    private RemoteViews notificationViews;

    private BroadcastReceiver jobBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Notification", "Room change received");

            if (intent.getAction().contains(BUTTON_PRESSED)) {
                String index = intent.getAction().substring(BUTTON_PRESSED.length() + 1);
                Log.d("Index", index + " ");
                int lightId = Integer.parseInt(index);
                RoomManager.modifyLightStatus(RoomManager.getCurrentRoom(), lightId, (RoomManager.getCurrentRoom().lights.get(lightId).status == 0) ? 1 : 0, true);
            }

            updateNotification();

            NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
            //android.app.NotificationManager mNotificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(notificationId, mBuilder.build());
        }
    };

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter filter = new IntentFilter(ROOM_SELECTED);
        filter.addAction(ROOM_UPDATED);
        filter.addAction(BUTTON_PRESSED_0);
        filter.addAction(BUTTON_PRESSED_1);
        filter.addAction(BUTTON_PRESSED_2);
        filter.addAction(BUTTON_PRESSED_3);
        filter.addAction(BUTTON_PRESSED_4);
        filter.addAction(BUTTON_PRESSED_5);
        registerReceiver(jobBroadcastReceiver, filter);

        Log.d("Notification", "Registered intent receiver");
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(jobBroadcastReceiver);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //Intent deleteIntent = new Intent(this, NotificationPressedReciever.class);
        //PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        updateNotification();
        startForeground(notificationId, mBuilder.build());

        while(true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
/*
        Notification notification = mBuilder.build();
        android.app.NotificationManager mNotificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(101, mBuilder.build());
*/
    }

    public void updateNotification() {

        notificationViews = new RemoteViews(getPackageName(), R.layout.persistent_notification);

        Room room = RoomManager.getCurrentRoom();
        notificationViews.setTextViewText(R.id.notification_roomText, "Room: " + ((room != null)? room.name : "No room"));

        updateNotificationViewForRoom(notificationViews, R.id.notification_light_button1, (room != null && room.lights != null && room.lights.size() > 0), room, 0);
        updateNotificationViewForRoom(notificationViews, R.id.notification_light_button2, (room != null && room.lights != null && room.lights.size() > 1), room, 1);
        updateNotificationViewForRoom(notificationViews, R.id.notification_light_button3, (room != null && room.lights != null && room.lights.size() > 2), room, 2);
        updateNotificationViewForRoom(notificationViews, R.id.notification_light_button4, (room != null && room.lights != null && room.lights.size() > 3), room, 3);
        updateNotificationViewForRoom(notificationViews, R.id.notification_light_button5, (room != null && room.lights != null && room.lights.size() > 4), room, 4);

        //building the notification
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_rhome_icon)
                .setContentTitle("Room control")
                .setContentText("Press to open application")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(false)
                .setOngoing(true)
                .setContent(notificationViews);

        Intent notificationIntent = new Intent(this, RoomControl.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
    }

    private void updateNotificationViewForRoom(RemoteViews notificationView, int id, boolean shown, Room room, int index) {
        if (!shown) {
            notificationView.setViewVisibility(id, View.GONE);
            return;
        }

        notificationView.setViewVisibility(id, View.VISIBLE);
        notificationView.setTextViewText(id, room.lights.get(index).name);
        notificationView.setTextViewCompoundDrawables(id, 0, (room.lights.get(index).status == 0) ? R.drawable.ic_bulb_notif_white : R.drawable.ic_bulb_notif_yellow, 0, 0);

        Intent buttonIntent = new Intent(BUTTON_PRESSED + "_" + index);
        PendingIntent pendingButtonIntent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0);

        notificationView.setOnClickPendingIntent(id, pendingButtonIntent);
    }
}