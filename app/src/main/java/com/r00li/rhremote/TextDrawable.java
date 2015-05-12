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

public class TextDrawable extends Drawable {

    private final String text;
    private final Paint paint;
    private final Drawable icon;

    public TextDrawable(String text, Drawable icon) {

        this.text = text;

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
        circlePaint.setColor(Color.WHITE);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(1);
        canvas.drawCircle(bounds.centerX(),bounds.centerY(),bounds.height()/2,circlePaint);

        canvas.drawText(text, bounds.centerX() - 15f /*just a lazy attempt to centre the text*/ * text.length(), bounds.centerY() + 15f, paint);

        icon.setBounds(bounds.left+30, bounds.top+30, bounds.right-30, bounds.bottom-30);
        Log.w("BOUND", "Bounds: " + bounds.left + " " + bounds.top + " " + bounds.right + " " + bounds.bottom);
        icon.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
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