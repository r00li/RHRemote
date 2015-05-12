package com.lukedeighton.wheelview.transformer;

import android.graphics.drawable.Drawable;

import com.lukedeighton.wheelview.WheelView;

public class FadingSelectionTransformer implements WheelSelectionTransformer  {

    @Override
    public void transform(Drawable drawable, WheelView.ItemState itemState) {
        float relativePosition = Math.abs(itemState.getRelativePosition());

        if (relativePosition < 1f) {
            drawable.setAlpha(255);
        }
        else {
            drawable.setAlpha(0);
        }
    }
}
