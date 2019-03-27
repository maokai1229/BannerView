package com.ethan.play.rcyclerbanner.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ethan.play.rcyclerbanner.base.adapter.base.ViewHolder;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adkins.zhang on 2017/6/20.
 */
public class MultiItemTypeAdapter<T extends IMulTypeHelper> extends RecyclerView.Adapter<ViewHolder> {
    protected Context mContext;
    protected List<T> adapterDataList;
    protected OnItemClickListener mOnItemClickListener;
    protected OnItemViewClickListener onItemViewClickListener;


    public MultiItemTypeAdapter(Context context) {
        mContext = context;
        adapterDataList = new ArrayList<>();
    }

    public MultiItemTypeAdapter(Context context, List<T> datas) {
        mContext = context;
        adapterDataList = datas;
    }

    public void setAdapterData(List dataList) {
        adapterDataList.clear();
        addAdapterData(dataList);
    }

    public void addAdapterData(List dataList) {
        adapterDataList.addAll(dataList);
    }

    public void addAdapterData(int index, List dataList) {
        adapterDataList.addAll(index, dataList);
    }

    public void removeAdapterData(int position) {
        if (position < adapterDataList.size()) {
            adapterDataList.remove(position);
            notifyDataSetChanged();
        }
    }

    public void clearAdapterData() {
        adapterDataList.clear();
        notifyDataSetChanged();
    }

    public void addAdapterData(T data) {
        adapterDataList.add(data);
    }

    public void addAdapterData(int index, T data) {
        adapterDataList.add(index, data);
    }

    @Override
    public int getItemViewType(int position) {
        if (adapterDataList != null && !adapterDataList.isEmpty()) {
            return adapterDataList.get(position).getItemLayoutId(mContext);
        }
        return adapterDataList.get(position).getItemLayoutId(mContext);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = ViewHolder.createViewHolder(mContext, parent, viewType);
        onViewHolderCreated(holder, holder.getConvertView());
        setListener(parent, holder, viewType);
        return holder;
    }

    public void onViewHolderCreated(ViewHolder holder, View itemView) {

    }

    public void convert(ViewHolder holder, T t) {
        t.onBind(holder);
    }

    protected boolean isEnabled(int viewType) {
        return true;
    }


    protected void setListener(final ViewGroup parent, final ViewHolder viewHolder, int viewType) {
        if (!isEnabled(viewType)) return;
        if (onItemViewClickListener != null)
            viewHolder.setOnViewClickListener(onItemViewClickListener);
        viewHolder.getConvertView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    int position = viewHolder.getAdapterPosition();
                    mOnItemClickListener.onItemClick(v, viewHolder, position);
                }
            }
        });

        viewHolder.getConvertView().setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemClickListener != null) {
                   // v.performClick();
                   // return true;
                    int position = viewHolder.getAdapterPosition();
                    return mOnItemClickListener.onItemLongClick(v, viewHolder, position);
                }
                return false;
            }
        });
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        convert(holder, adapterDataList.get(position));
    }

    @Override
    public int getItemCount() {
        int itemCount = adapterDataList.size();
        return itemCount;
    }

    public void removeAllData() {
        adapterDataList.clear();
        notifyDataSetChanged();
    }

    public List<T> getAdapterList() {
        return adapterDataList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ViewHolder holder, int position);

        boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    public interface OnItemViewClickListener {
        void onItemViewClick(View v);
    }
}
