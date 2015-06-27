package com.girls.activity;

import android.app.Activity;
import android.graphics.Matrix;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.girls.R;
import com.girls.event.DisplayEvent;
import com.girls.event.EmptyEvent;
import com.girls.event.FilterEvent;
import com.girls.event.FilterImgEvent;
import com.girls.event.HideEvent;
import com.girls.event.PasteTagEvent;
import com.girls.event.TagsEvent;
import com.girls.fragment.BaseFragment;
import com.girls.fragment.FilterFragment;
import com.girls.fragment.FunctionFragment;
import com.girls.fragment.TagsFragment;
import com.girls.tags.PasterObject;
import com.girls.tags.ViewOperation;
import com.girls.tools.AndroidTools;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.io.FileNotFoundException;
import java.io.InputStream;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

/**
 * Created by gcl on 2015/4/26.
 */
public class EditActivity extends BaseActivity implements View.OnClickListener{

    private GPUImageView mImgV;
    private static int MAXSIZE = 1920;
    private  Uri mUri;
    private ImageView mBackImg;
    private FrameLayout mFlContainer;
    private FunctionFragment mFunFg;
    private FilterFragment mFilterFg;
    private TagsFragment mTagsFg;
    private FrameLayout mFlView;
    private ViewOperation mViewOperation;
    private RelativeLayout mRlTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mImgV = (GPUImageView)findViewById(R.id.img);
        mBackImg = (ImageView)findViewById(R.id.title_img);
        mFlContainer = (FrameLayout)findViewById(R.id.container);
        mRlTitle = (RelativeLayout)findViewById(R.id.title_layout);
        mFlView = (FrameLayout)findViewById(R.id.view_layout);
        mViewOperation = (ViewOperation)findViewById(R.id.viewOpertion);
        mBackImg.setOnClickListener(this);
        Intent it = getIntent();
        mUri = it.getParcelableExtra("uri");
        toDisplayImg(getImgInputStream(mUri));
        mFunFg = new FunctionFragment();
        mFilterFg = new FilterFragment();
        mTagsFg = new TagsFragment();
        replaceFragment(mFunFg);
//
    }

    private InputStream getImgInputStream(Uri uri){
        try {
            if (uri.getScheme().equals("file")) {
                return new java.io.FileInputStream(uri.getPath());
            } else {
                return getContentResolver().openInputStream(uri);
            }
        } catch (FileNotFoundException ex) {
            System.out.println("++++");
            System.out.println(ex.toString());
            System.out.println(ex.getCause());
            System.out.println(ex.getMessage());
            System.out.println(ex.getStackTrace());
            return null;
        }
    }

    private void toDisplayImg(InputStream in){
        if(in == null){
            return;
        }
        BitmapFactory.Options factoryOptions = new BitmapFactory.Options();
        factoryOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(in,null,factoryOptions);
        float sampleseize = 1;
        sampleseize = (float)factoryOptions.outWidth / AndroidTools.getScreenWidth(this);
/*        while(factoryOptions.outHeight / sampleseize > MAXSIZE
                && factoryOptions.outWidth / sampleseize > MAXSIZE){
            sampleseize = sampleseize << 1;
        }*/
        mFlView.getLayoutParams().height =(int)(AndroidTools.getScreenHeight(this) * sampleseize);
        mFlView.getLayoutParams().width = AndroidTools.getScreenWidth(this);
//        factoryOptions.inSampleSize = (int)sampleseize;
        factoryOptions.inJustDecodeBounds = false;
        in = getImgInputStream(mUri);
        Bitmap map = BitmapFactory.decodeStream(in, null, factoryOptions);
        mImgV.setImage(mUri);
 //       mImgV.setImageBitmap(map);
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment,fragment.getClass().getSimpleName());
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(fragment.getClass().getSimpleName());
        ft.commit();
    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.title_img:

                finish();
                break;
        }

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        Fragment fragment = getCurrentFragment();
        BaseFragment baseFragment = (BaseFragment)fragment;
        if(FunctionFragment.class.getSimpleName().equals(baseFragment.getClass().getSimpleName())){
            finish();
        }
        else{
            baseFragment.onBackPressed();
        }
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int i = fragmentManager.getBackStackEntryCount() - 1; i >= 0; i--) {
            String tag = fragmentManager.getBackStackEntryAt(i).getName();
            Fragment curFragment = getSupportFragmentManager() .findFragmentByTag(tag);
            if (curFragment.isAdded()) {
                return curFragment;
            }
        }
        return null;
    }

    public void onEventMainThread(FilterEvent event){
        Log.i("EditActivity","FilterEvent");
        replaceFragment(mFilterFg);
    }
    public void onEventMainThread(TagsEvent event){
        Log.i("EditActivity","TagsEvent");
        replaceFragment(mTagsFg);
    }

    public void onEventMainThread(PasteTagEvent event){
        PasterObject object = new PasterObject(this,BitmapFactory.decodeResource(getResources(),event.id));
        Matrix matrix = new Matrix();
        matrix.reset();
        matrix.postTranslate(50 * mViewOperation.getObjectSize(), 50 * mViewOperation.getObjectSize());
        object.setInitStatus(matrix,false);
        object.setOnRedrawListener(mViewOperation);
        mViewOperation.addObjectDrawable(object);
    }

    public void onEventMainThread(FilterImgEvent event){
        mImgV.setFilter(event.mFilter);
        mImgV.requestRender();

    }

    public void onEventMainThread(HideEvent event){
        int topHeight = mRlTitle.getHeight();
        int bottomHeight = mFlContainer.getHeight();
        ViewPropertyAnimator.animate(mRlTitle).translationY(-topHeight).setDuration(10).start();
        ViewPropertyAnimator.animate(mFlContainer).translationY(bottomHeight).setDuration(10).start();
    }
    public void onEventMainThread(DisplayEvent event){
        int topHeight = mRlTitle.getHeight();
        int bottomHeight = mFlContainer.getHeight();
        ViewPropertyAnimator.animate(mRlTitle).translationY(0).setDuration(10).start();
        ViewPropertyAnimator.animate(mFlContainer).translationY(0).setDuration(10).start();
    }

}
