package com.ethan.play.rcyclerbanner.rebanner;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ethan.play.rcyclerbanner.R;

import java.util.Random;


/**
 * @author Ethan.mao
 * @date 2019/3/21
 */
public class BannerAdapter extends RecyclerView.Adapter {
    private final Random mRandom = new Random();
    private  int[] mColors = null;
    public   int[] mPosition = null;
    private int mItemsCount = 100;
    private Context mContext;
  //  private int[] mColors = {R.mipmap.item1,R.mipmap.item2,R.mipmap.item3,R.mipmap.item4,R.mipmap.item5,R.mipmap.item6};

    private onItemClick clickCb;

    public BannerAdapter(Context context) {
        mColors = new int[mItemsCount];
        mPosition = new int[mItemsCount];
        for (int i = 0; mItemsCount > i; ++i) {
            //noinspection MagicNumber
            mColors[i] = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
            mPosition[i] = i;
        }
        mContext = context;
    }

    public BannerAdapter(Context context, onItemClick cb) {
        mContext = context;
        clickCb = cb;
    }

    public void setOnClickLstn(onItemClick cb) {
        this.clickCb = cb;
    }

    @Override
    public BannerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_item, parent, false);
        return new BannerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
    //    viewHolder = (BannerViewHolder) viewHolder;
//        Glide.with(mContext).load(mColors[i % mColors.length])
//                .into(((BannerViewHolder) viewHolder).img);
        ((BannerViewHolder) viewHolder).img.setBackgroundColor(mColors[i]);
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              Toast.makeText(mContext, "点击了："+i, Toast.LENGTH_SHORT).show();
//                if (clickCb != null) {
//                    clickCb.clickItem(i);
//                }
//            }
//        });
    }





    @Override
    public int getItemCount() {
        return 100;
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        public ImageView img;
        public BannerViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img);
        }
    }

    interface onItemClick {
        void clickItem(int pos);
    }
}
