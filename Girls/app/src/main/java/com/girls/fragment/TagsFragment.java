package com.girls.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.girls.R;
import com.girls.adapter.FilterRecycleAdapter;
import com.girls.adapter.TagsRecycleAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gcl on 2015/6/7.
 */
public class TagsFragment extends BaseFragment{

    private RecyclerView mRv;
    private List<Integer> mList = new ArrayList<Integer>();
    private TagsRecycleAdapter mAdapter;

    @Override
    protected View inflateLayout(LayoutInflater inflater,ViewGroup container) {
        return  inflater.inflate(R.layout.fragment_filter,container,false);
    }

    @Override
    protected void initView() {
        mRv = (RecyclerView)mVRoot.findViewById(R.id.recycler_view);
        mRv.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        mAdapter = new TagsRecycleAdapter(getActivity());
        initItems();
        mAdapter.addAllItems(mList);
        mRv.setAdapter(mAdapter);

    }

    private void initItems(){
        for(int i = 0;i< 10;i++){
            mList.add(R.drawable.img);
        }
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
    public boolean onBackPressed() {
        getFragmentManager().popBackStack();
        return true;
    }
}
