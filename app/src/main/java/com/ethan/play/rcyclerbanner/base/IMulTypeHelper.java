package com.ethan.play.rcyclerbanner.base;


import android.content.Context;

import com.ethan.play.rcyclerbanner.base.adapter.base.ViewHolder;


/**
 * Created by adkins.zhang on 2017/6/20.
 */

public interface IMulTypeHelper {
    int getItemLayoutId(Context context);

    void onBind(ViewHolder holder);
}
