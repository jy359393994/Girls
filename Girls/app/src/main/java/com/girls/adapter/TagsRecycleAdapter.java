package com.girls.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.girls.R;
import com.girls.event.PasteTagEvent;

import java.util.ArrayList;
import java.util.List;

import event.EventBus;

/**
 * ÌùÖ½
 * Created by gcl on 2015/6/6.
 */
public class TagsRecycleAdapter extends RecyclerView.Adapter<TagsRecycleAdapter.NormalViewHodler>{

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Integer> mList = new ArrayList<Integer>();
    public TagsRecycleAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void addAllItems(List<Integer> list){
        mList.clear();
        mList.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(NormalViewHodler holder, final int position) {
        holder.mIv.setImageResource(mList.get(position));
        holder.mIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TagsRecycleAdapter", "onClick --> position = " + position);
                EventBus.getDefault().post(new PasteTagEvent(position,mList.get(position)));
            }
        });
    }

    @Override
    public NormalViewHodler onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalViewHodler(mInflater.inflate(R.layout.adapter_item_tags,parent,false));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class  NormalViewHodler extends RecyclerView.ViewHolder{
        public ImageView mIv;
        public NormalViewHodler(View itemView) {
            super(itemView);
            mIv = (ImageView)itemView.findViewById(R.id.imageview);
        }
    }
}
