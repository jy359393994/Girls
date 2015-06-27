package com.girls.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.girls.R;
import com.girls.event.FilterImgEvent;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import event.EventBus;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

/**
 * ÂË¾µ
 * Created by gcl on 2015/6/6.
 */
public class FilterRecycleAdapter extends RecyclerView.Adapter<FilterRecycleAdapter.NormalViewHodler>{

    private List<Item> mList = new ArrayList<Item>();
    private Context mContext;
    private LayoutInflater mInflater;
    private FilterImgEvent mFilterEvent;

    public FilterRecycleAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void addAllItems(List<Item>list){
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(NormalViewHodler holder, final int position) {
        holder.mCv.setImageResource(mList.get(position).mId);
        holder.mTvTitle.setText(mList.get(position).mTitle);
        holder.mCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("NormalViewHodler", "onClick --> position = " + position);
                if(mFilterEvent == null){
                    mFilterEvent = new FilterImgEvent();
                }
                mFilterEvent.mFilter = mList.get(position).mFilter;
                EventBus.getDefault().post(mFilterEvent);
            }
        });
    }

    @Override
    public NormalViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalViewHodler(mInflater.inflate(R.layout.adapter_item_filter,parent,false));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class NormalViewHodler extends RecyclerView.ViewHolder{
        private CircleImageView mCv;
        private TextView mTvTitle;

        public NormalViewHodler(View itemView) {
            super(itemView);
            mCv = (CircleImageView)itemView.findViewById(R.id.circleview);
            mTvTitle = (TextView)itemView.findViewById(R.id.title);
        }
    }

    public static class Item{
        public int mId;
        public GPUImageFilter mFilter;
        public String mTitle;
        public Item(int id,String title,GPUImageFilter filter){
            mId = id;
            mTitle = title;
            mFilter = filter;
        }
    }
}
