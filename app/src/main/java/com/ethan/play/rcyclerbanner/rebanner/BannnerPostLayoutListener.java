package com.ethan.play.rcyclerbanner.rebanner;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

/**
 * @author Ethan.mao
 * @date 2019/3/22
 */
public class BannnerPostLayoutListener implements CarouselLayoutManager.PostLayoutListener {


    @Override
    public ItemTransformation transformChild(@NonNull final View child, final float itemPositionToCenterDiff, final int orientation) {
      //  float scale = (float) (2 * (2 * -StrictMath.atan(Math.abs(itemPositionToCenterDiff) + 1.0) / Math.PI + 1));
        float scale = (float) (2 * (2 * -StrictMath.atan(Math.abs(itemPositionToCenterDiff/5) + 1.0) / Math.PI + 1));

        // because scaling will make view smaller in its center, then we should move this item to the top or bottom to make it visible
        final float translateY;
        final float translateX;
        if (CarouselLayoutManager.VERTICAL == orientation) {
            final float translateYGeneral = child.getMeasuredHeight() * (1 - scale) / 2f;
            translateY = Math.signum(itemPositionToCenterDiff) * translateYGeneral;
            translateX = 0;
        }
        else {
            final float translateXGeneral = child.getMeasuredWidth() * (1 - scale) * 6.8f;
            translateX = Math.signum(itemPositionToCenterDiff) * translateXGeneral;
            translateY = 0;
        }

        return new ItemTransformation(scale, scale, translateX, translateY);
    }



}
