package com.ethan.play.rcyclerbanner.rebanner;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;


import com.ethan.play.rcyclerbanner.R;
import com.ethan.play.rcyclerbanner.base.CommonAdapter;

import java.util.Locale;

/**
 * @author Ethan.mao
 * @date 2019/3/26
 */
public class BannerView extends RecyclerView {
    private CarouselLayoutManager layoutManager;
    private SnapHelper snapHelper;
    private int padding;
    private View view;

    public BannerView(Context context) {
        this(context,null);
    }

    public BannerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.BannerView);
        padding = (int) typedArray.getDimension(R.styleable.BannerView_paddingLeftRight,10);
        typedArray.recycle();
        initView();
    }

    private void initView() {
       view = View.inflate(getContext(), R.layout.layout_item,null);
       layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
       snapHelper = new BannerSnapHelper();

    }

    public void setPaddingLeftRight(int padding) {
        this.padding = padding;
        if (view!=null){
            view.setPadding(padding,0,padding,0);
            view.invalidate();
        }
    }



    public void init(CommonAdapter adapter) {
        // enable zoom effect. this line can be customized
        layoutManager.setPostLayoutListener(new BannnerPostLayoutListener());

        layoutManager.setMaxVisibleItems(1);

        this.setLayoutManager(layoutManager);
        // we expect only fixed sized item for now
        this.setHasFixedSize(false);
        // sample adapter with random data
        this.setAdapter(adapter);
        // enable center post scrolling
        // enable center post touching on item and item click listener
//        DefaultChildSelectionListener.initCenterItemListener(new DefaultChildSelectionListener.OnCenterItemClickListener() {
//            @Override
//            public void onCenterItemClicked(@NonNull final RecyclerView recyclerView, @NonNull final CarouselLayoutManager carouselLayoutManager, @NonNull final View v) {
//                final int position = recyclerView.getChildLayoutPosition(v);
//                final String msg = String.format(Locale.US, "Item %1$d was clicked", position);
//
//            }
//        }, this, layoutManager);

       snapHelper.attachToRecyclerView(this);
//        layoutManager.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {
//
//            @Override
//            public void onCenterItemChanged(final int adapterPosition) {
//                if (CarouselLayoutManager.INVALID_POSITION != adapterPosition) {
//
//                    // final int value =(BannerAdapter) adapter.mPosition[adapterPosition];
////                    adapter.mPosition[adapterPosition] = (value % 10) + (value / 10 + 1) * 10;
////                    adapter.notifyItemChanged(adapterPosition);
//                }
//            }
//        });

   //   this.addOnScrollListener(new CenterScrollListener());

    }

}
