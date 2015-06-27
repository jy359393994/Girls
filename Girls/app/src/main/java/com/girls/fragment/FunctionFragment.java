package com.girls.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.girls.R;
import com.girls.event.FilterEvent;
import com.girls.event.TagsEvent;

import event.EventBus;

/**
 * Created by gcl on 2015/6/7.
 */
public class FunctionFragment extends BaseFragment implements View.OnClickListener{


    private TextView mTvCrop;
    private TextView mTvFilter;
    private TextView mTvTags;

    @Override
    protected View inflateLayout(LayoutInflater inflater,ViewGroup container) {
        return  inflater.inflate(R.layout.fragment_function,container,false);
    }

    @Override
    protected void initView() {
        mTvCrop = (TextView)mVRoot.findViewById(R.id.crop);
        mTvFilter = (TextView)mVRoot.findViewById(R.id.filter);
        mTvTags = (TextView)mVRoot.findViewById(R.id.tags);
        mTvCrop.setOnClickListener(this);
        mTvFilter.setOnClickListener(this);
        mTvTags.setOnClickListener(this);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.crop:

                break;
            case R.id.filter:
                EventBus.getDefault().post(new FilterEvent());
                break;
            case R.id.tags:
                EventBus.getDefault().post(new TagsEvent());
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }
}
