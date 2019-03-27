package com.ethan.play.rcyclerbanner.base.adapter.base;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by adkins.zhang on 2017/7/14.
 */

public abstract class BaseFooterView extends View {
    private OnFootRetryListener retryListener;

    public BaseFooterView(Context context) {
        this(context, null);
    }

    public BaseFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setOnFootRetryListener(OnFootRetryListener retryListener) {
        this.retryListener = retryListener;
    }

    public OnFootRetryListener getRetryListener() {
        return retryListener;
    }

    public abstract void setNormalStatus();

    public abstract void setLoadFailedStatus();

    public abstract void setLoadingStatus();

    public abstract void setLoadSuccessStatus();

    public interface OnFootRetryListener {
        void onLoadMoreRequested();
    }
}
