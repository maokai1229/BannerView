package com.ethan.play.rcyclerbanner;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.ethan.play.rcyclerbanner.bean.BanberItem;
import com.ethan.play.rcyclerbanner.rebanner.BannerItemAdapter;
import com.ethan.play.rcyclerbanner.rebanner.BannerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private final Random mRandom = new Random();
    private RecyclerView bannerRv;
    private BannerView bannerView;
    BannerItemAdapter adapter;
    List<BanberItem> itemList;
    private int mItemsCount = 10;


    private  int[] mColors = null;
    public   int[] mPosition = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itemList = new ArrayList<>();

        mColors = new int[mItemsCount];
        mPosition = new int[mItemsCount];

        for (int i = 0; mItemsCount > i; ++i) {
            //noinspection MagicNumber
            mColors[i] = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
            mPosition[i] = i;
            BanberItem banberItem = new BanberItem();
            banberItem.setColor(mColors[i]);
            banberItem.setText(i+"");
            itemList.add(banberItem);
        }
        initView();

    }


    private void initView() {
        bannerView = findViewById(R.id.banner);
        adapter = new BannerItemAdapter(this,itemList);
        bannerView.init(adapter).setDelayTime(5000).startAutoPlay();
    }

}
