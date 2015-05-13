package com.r00li.rhremote;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.TypedValue;

public class TextDrawable extends Drawable {

    private final String text;
    private final Paint paint;
    private final Drawable icon;
    private Context context;
    private boolean selected;

    public TextDrawable(String text, Drawable icon, Context context) {

        this.text = text;
        this.context = context;
        this.icon = icon;

        this.paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.LEFT);

    }

    @Override
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        Paint circlePaint = new Paint();
        if (selected)
            circlePaint.setColor(0x0284f09);
        else
            circlePaint.setColor(0xFFffc719);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setStrokeWidth(3);
        canvas.drawCircle(bounds.centerX(),bounds.centerY(),bounds.height()/2,circlePaint);

        circlePaint.setColor(Color.WHITE);
        circlePaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(bounds.centerX(),bounds.centerY(),bounds.height()/2,circlePaint);

        //canvas.drawText(text, bounds.centerX() - 15f /*just a lazy attempt to centre the text*/ * text.length(), bounds.centerY() + 15f, paint);

        int centerX = (bounds.left + bounds.right)/2;
        int centerY = (bounds.top + bounds.bottom)/2;
        icon.setBounds(centerX-icon.getIntrinsicWidth()/2, centerY-icon.getIntrinsicHeight()/2, centerX+icon.getIntrinsicWidth()/2, centerY+icon.getIntrinsicHeight()/2);
        icon.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        if (alpha == 255)
        {
            selected = true;
        }
        else
        {
            selected = false;
        }
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        paint.setColorFilter(cf);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}