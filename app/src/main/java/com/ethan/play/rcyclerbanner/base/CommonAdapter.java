package com.ethan.play.rcyclerbanner.base;

import android.content.Context;

import java.util.List;

/**
 * Created by adkins.zhang on 2017/6/20.
 */
public abstract class CommonAdapter<T extends IMulTypeHelper> extends MultiItemTypeAdapter<T> {

    public CommonAdapter(final Context context) {
        super(context);
    }

    public CommonAdapter(final Context context, List<T> mDatas) {
        super(context, mDatas);
    }

}
