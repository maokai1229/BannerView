package com.ethan.play.rcyclerbanner.rebanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

import com.ethan.play.rcyclerbanner.R;

/**
 * @author Ethan.mao
 * @date 2019/3/27
 */
public class BannerItemView extends RelativeLayout {


    public BannerItemView(Context context) {
        this(context,null);
    }

    public BannerItemView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BannerItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BannerView);
        int padding = (int) typedArray.getDimension(R.styleable.BannerView_paddingLeftRight, 10);
        typedArray.recycle();
        setPadding(padding,0,padding,0);
    }


    public void setPaddingLeftRight(int padding) {

            setPadding(padding,0,padding,0);
            requestLayout();

    }
}
