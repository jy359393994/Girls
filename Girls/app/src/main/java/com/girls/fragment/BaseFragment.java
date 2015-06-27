package com.girls.fragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by gcl on 2015/6/7.
 */
public abstract class BaseFragment extends Fragment{
    protected View mVRoot;


//    private static String LOG = "BaseFragment";
    public BaseFragment() {
        super();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.i(getClass().getSimpleName(), "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(getClass().getSimpleName(), "onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i(getClass().getSimpleName(), "onCreateView");
        if(mVRoot == null){
            mVRoot = inflateLayout(inflater,container);
            initView();
        }
        ViewGroup parent = (ViewGroup)mVRoot.getParent();
        if(parent != null) {
            parent.removeAllViews();
        }
        return mVRoot;
    }

    abstract protected  View inflateLayout(LayoutInflater inflater,ViewGroup container);

    abstract protected void initView();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.i(getClass().getSimpleName(), "onViewCreated");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(getClass().getSimpleName(), "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(getClass().getSimpleName(), "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(getClass().getSimpleName(), "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(getClass().getSimpleName(), "onCreateView");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(getClass().getSimpleName(), "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(getClass().getSimpleName(), "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(getClass().getSimpleName(), "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(getClass().getSimpleName(), "onDetach");
    }

    public boolean onBackPressed(){
        return false;
    }

}
