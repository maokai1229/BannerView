package com.ethan.play.rcyclerbanner.rebanner;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ethan.play.rcyclerbanner.base.CommonAdapter;

import java.util.Locale;

/**
 * @author Ethan.mao
 * @date 2019/3/22
 */
public class ReBanner {

    private CarouselLayoutManager layoutManager;
    private LinearSnapHelper snapHelper;

    public static ReBanner newInstance (){
        return new ReBanner();
    }

    public void init(RecyclerView bannerRv, CommonAdapter adapter){
        snapHelper = new LinearSnapHelper();
        layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
        initRecyclerView(bannerRv, layoutManager,adapter);
    }

    private void initRecyclerView(final RecyclerView recyclerView, final CarouselLayoutManager layoutManager, CommonAdapter adapter) {
        // enable zoom effect. this line can be customized
        layoutManager.setPostLayoutListener(new BannnerPostLayoutListener());

        layoutManager.setMaxVisibleItems(1);

        recyclerView.setLayoutManager(layoutManager);
        // we expect only fixed sized item for now
        recyclerView.setHasFixedSize(true);
        // sample adapter with random data
        recyclerView.setAdapter(adapter);
        // enable center post scrolling
        recyclerView.addOnScrollListener(new CenterScrollListener());
        // enable center post touching on item and item click listener
        DefaultChildSelectionListener.initCenterItemListener(new DefaultChildSelectionListener.OnCenterItemClickListener() {
            @Override
            public void onCenterItemClicked(@NonNull final RecyclerView recyclerView, @NonNull final CarouselLayoutManager carouselLayoutManager, @NonNull final View v) {
                final int position = recyclerView.getChildLayoutPosition(v);
                final String msg = String.format(Locale.US, "Item %1$d was clicked", position);

            }
        }, recyclerView, layoutManager);

        snapHelper.attachToRecyclerView(recyclerView);
        layoutManager.addOnItemSelectionListener(new CarouselLayoutManager.OnCenterItemSelectionListener() {

            @Override
            public void onCenterItemChanged(final int adapterPosition) {
                if (CarouselLayoutManager.INVALID_POSITION != adapterPosition) {

                     // final int value =(BannerAdapter) adapter.mPosition[adapterPosition];
//                    adapter.mPosition[adapterPosition] = (value % 10) + (value / 10 + 1) * 10;
//                    adapter.notifyItemChanged(adapterPosition);
                }
            }
        });
    }





}
