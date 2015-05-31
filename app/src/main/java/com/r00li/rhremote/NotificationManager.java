package com.r00li.rhremote;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

/**
 * Created by roli on 30/05/15.
 */
public class NotificationManager {

    public static Context context;

    static void showToastMessage(String message) {
        Toast t = Toast.makeText(context, message, Toast.LENGTH_LONG);
        t.show();
    }

    static void showAlertDialogMessage(String message) {
        new AlertDialog.Builder(context)
                .setTitle("Something went wrong!")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

}
