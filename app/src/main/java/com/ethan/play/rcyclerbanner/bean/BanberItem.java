package com.ethan.play.rcyclerbanner.bean;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.ethan.play.rcyclerbanner.R;
import com.ethan.play.rcyclerbanner.base.IMulTypeHelper;
import com.ethan.play.rcyclerbanner.base.adapter.base.ViewHolder;

/**
 * @author Ethan.mao
 * @date 2019/3/22
 */
public class BanberItem implements IMulTypeHelper {

    private int color;
    private String text;

    @Override
    public int getItemLayoutId(Context context) {
        return R.layout.layout_item;
    }

    @Override
    public void onBind(ViewHolder holder) {
        ImageView imageView = holder.getView(R.id.img);
        TextView textView = holder.getView(R.id.text);

        imageView.setBackgroundColor(getColor());
        textView.setText(getText());
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
