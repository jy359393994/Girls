package com.girls.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.girls.R;
import com.girls.adapter.FilterRecycleAdapter;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageColorInvertFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueBlendFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageRGBFilter;

/**
 * Created by gcl on 2015/6/7.
 */
public class FilterFragment extends BaseFragment{

    private RecyclerView mRv;
    private List<FilterRecycleAdapter.Item> mList = new ArrayList<FilterRecycleAdapter.Item>();
    private FilterRecycleAdapter mAdapter;

    @Override
    protected View inflateLayout(LayoutInflater inflater,ViewGroup container) {
        return inflater.inflate(R.layout.fragment_filter,container,false);
    }

    @Override
    protected void initView() {
        mRv = (RecyclerView)mVRoot.findViewById(R.id.recycler_view);
        mRv.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false));
        mAdapter = new FilterRecycleAdapter(getActivity());
        initItems();
        mAdapter.addAllItems(mList);
        mRv.setAdapter(mAdapter);

    }

    private void initItems(){
        mList.add(new FilterRecycleAdapter.Item(R.drawable.img, "Constrast",new GPUImageContrastFilter() ));
        mList.add(new FilterRecycleAdapter.Item(R.drawable.img, "Invert",new GPUImageColorInvertFilter() ));
        mList.add(new FilterRecycleAdapter.Item(R.drawable.img, "Brightness",new GPUImageBrightnessFilter() ));
        mList.add(new FilterRecycleAdapter.Item(R.drawable.img, "Grayscale",new GPUImageGrayscaleFilter() ));
        mList.add(new FilterRecycleAdapter.Item(R.drawable.img, "Hue",new GPUImageHueFilter() ));
        mList.add(new FilterRecycleAdapter.Item(R.drawable.img, "Gamma",new GPUImageGammaFilter() ));
        for(int i = 0;i< 10;i++){
            mList.add(new FilterRecycleAdapter.Item(R.drawable.img,"RGB",new GPUImageRGBFilter()));
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
