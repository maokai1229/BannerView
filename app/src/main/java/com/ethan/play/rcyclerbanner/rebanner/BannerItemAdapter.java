package com.ethan.play.rcyclerbanner.rebanner;

import android.content.Context;

import com.ethan.play.rcyclerbanner.bean.BanberItem;
import com.ethan.play.rcyclerbanner.base.CommonAdapter;

import java.util.List;
import java.util.Random;

/**
 * @author Ethan.mao
 * @date 2019/3/22
 */
public class BannerItemAdapter extends CommonAdapter {

    public BannerItemAdapter(Context context, List<BanberItem> mDatas) {
        super(context, mDatas);
    }

}
