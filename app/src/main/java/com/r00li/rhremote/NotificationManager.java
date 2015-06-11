package com.r00li.rhremote;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by roli on 30/05/15.
 */
public class NotificationManager {

    public static Context context;
    private static Toast toast;

    static void showToastMessage(String message) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        }
        else {
            toast.setText(message);
        }

        toast.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL, 0, 0);
        View toastView = toast.getView();
        TextView messageView = (TextView)toastView.findViewById(android.R.id.message);
        messageView.setTextColor(Color.WHITE);
        messageView.setShadowLayer(0,0,0,0);
        toastView.setBackgroundResource(R.drawable.rounded_drawable);

        toast.show();
    }

    static void showAlertDialogMessage(String message) {
        new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.alertBoxTitle))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

}
