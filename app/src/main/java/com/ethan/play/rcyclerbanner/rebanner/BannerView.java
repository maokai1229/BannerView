package com.ethan.play.rcyclerbanner.rebanner;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;

import android.support.annotation.Nullable;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


import com.ethan.play.rcyclerbanner.R;
import com.ethan.play.rcyclerbanner.base.CommonAdapter;

import java.lang.ref.WeakReference;
import java.util.Locale;

import java.util.logging.LogRecord;

/**
 * @author Ethan.mao
 * @date 2019/3/26
 */
public class BannerView extends RecyclerView {
    private CarouselLayoutManager layoutManager;
    private BannerSnapHelper snapHelper;
    private BannerView bannerView;
    private static final int AUTO_PLAY = 1001;
    private static final int PLAY_LEFT = 1004;
    private static final int PLAY_RIGHT = 1005;
    private Handler mHandler;
    public static int DEFAULT_TIME = 2000;
    private boolean playing;
    private CommonAdapter mAdapter;
    private float x;

    public BannerView(Context context) {
        this(context,null);
    }

    public BannerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BannerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        mHandler = new BannerHandler(this);

    }
    private BannerView initView() {
       layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
       snapHelper = new BannerSnapHelper();
       bannerView = this;
       return this;
    }

    public BannerView setDelayTime(int time){
        DEFAULT_TIME = time;
        return this;
    }

    public BannerView startAutoPlay(){
        playing = true;
        if (playing) {
            mHandler.sendEmptyMessageDelayed(AUTO_PLAY, DEFAULT_TIME);
        }
        return this;
    }


    public void stopAutoPlay(){
        mHandler.removeMessages(AUTO_PLAY);
        playing = false;
    }


    public BannerView init(CommonAdapter adapter) {
        // enable zoom effect. this line can be customized
        layoutManager.setPostLayoutListener(new BannnerPostLayoutListener());
        layoutManager.setMaxVisibleItems(1);
        this.setLayoutManager(layoutManager);
        // we expect only fixed sized item for now
        this.setHasFixedSize(false);
        // sample adapter with random data
        this.setAdapter(adapter);
        this.mAdapter = adapter;
        // enable Log post scrolling
       // CardPagerSnapHelper cardPagerSnapHelper = new CardPagerSnapHelper();
       // cardPagerSnapHelper.attachToRecyclerView(this);


        snapHelper.attachToRecyclerView(this);
        return this;
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setPlaying(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setPlaying(false);
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }


    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            setPlaying(true);
        } else {
            setPlaying(false);
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHandler.removeMessages(AUTO_PLAY);
                setPlaying(false);
                break;
            case MotionEvent.ACTION_UP:
//                if (ev.getX() - x >200){
//                    mHandler.sendEmptyMessage(PLAY_RIGHT);
//                }else if (ev.getX() - x <-200){
//                    mHandler.sendEmptyMessage(PLAY_LEFT);
//                }else
                    setPlaying(true);
                    mHandler.sendEmptyMessageDelayed(AUTO_PLAY,DEFAULT_TIME);
                break;
            case MotionEvent.ACTION_CANCEL:
                setPlaying(true);
                break;
            case MotionEvent.ACTION_MOVE:
                setPlaying(false);
        }
        return super.dispatchTouchEvent(ev);
    }



    static class BannerHandler extends Handler{
        WeakReference<BannerView> mWeakReference;

        public  BannerHandler (BannerView  bannerView){
            mWeakReference = new WeakReference<>(bannerView);
        }

        @Override
        public void handleMessage(Message msg) {
            BannerView bannerView = mWeakReference.get();

            int indexPositon = bannerView.snapHelper.getCenterViewPosition(bannerView.layoutManager);


            if (msg.what == AUTO_PLAY){
                if (bannerView.playing) {
                    if (indexPositon == bannerView.mAdapter.getItemCount()-1){
                        // 最后一个
                        indexPositon = 0;
                    }else {
                        ++indexPositon;
                    }
                    bannerView.smoothScrollToPosition(indexPositon);
                }
                sendEmptyMessageDelayed(AUTO_PLAY, DEFAULT_TIME);
            }
//            else if (msg.what == PLAY_LEFT){
//                if (indexPositon == bannerView.mAdapter.getItemCount()-1){
//                    // 最后一个
//                    indexPositon = 0;
//                }else {
//                    ++indexPositon;
//                }
//                Log.e("fangixnag_l",indexPositon+"");
//                bannerView.smoothScrollToPosition(indexPositon);
//            }else if (msg.what == PLAY_RIGHT){
//                if (indexPositon == 0){
//                    // 最后一个
//                    indexPositon =  bannerView.mAdapter.getItemCount()-1;
//                }else {
//                    --indexPositon;
//                }
//                Log.e("fangixnag_r",indexPositon+"");
//                bannerView.smoothScrollToPosition(indexPositon);
//            }
        }

    }
}
