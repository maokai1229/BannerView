package com.ethan.play.rcyclerbanner.base.adapter.base;


/**
 * Created by adkins.zhang on 2017/7/14.
 */
public interface ItemViewDelegate<T> {

    int getItemViewLayoutId();

    boolean isForViewType(T item, int position);

    void convert(ViewHolder holder, T t, int position);

}
