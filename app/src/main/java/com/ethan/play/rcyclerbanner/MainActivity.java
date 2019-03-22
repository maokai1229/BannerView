package com.ethan.play.rcyclerbanner;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ethan.play.rcyclerbanner.rebanner.BannnerPostLayoutListener;
import com.ethan.play.rcyclerbanner.rebanner.CarouselLayoutManager;
import com.ethan.play.rcyclerbanner.rebanner.CenterScrollListener;
import com.ethan.play.rcyclerbanner.rebanner.DefaultChildSelectionListener;
import com.ethan.play.rcyclerbanner.rebanner.BannerAdapter;
import com.ethan.play.rcyclerbanner.rebanner.ReBanner;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView bannerRv;
    BannerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();

    }


    private void initView() {
        adapter = new BannerAdapter(this);
        bannerRv = findViewById(R.id.play_recycler_view);
        ReBanner reBanner = ReBanner.newInstance();
        reBanner.init(bannerRv, adapter);
    }

}
