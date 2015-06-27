package com.girls.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.girls.R;
import com.girls.event.FilterEvent;
import com.girls.event.PasteTagEvent;
import com.girls.event.TagsEvent;
import com.girls.fragment.FilterFragment;
import com.girls.fragment.FunctionFragment;
import com.girls.fragment.TagsFragment;
import com.girls.tags.PasterObject;
import com.girls.tags.ViewOperation;
import com.girls.tools.AndroidTools;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by gcl on 2015/4/26.
 */
public class EditActivity1 extends BaseActivity implements View.OnClickListener{

    private ImageView mImgV;
    private static int MAXSIZE = 1920;
    private  Uri mUri;
    private ImageView mBackImg;
    private FrameLayout mFlContainer;
    private FunctionFragment mFunFg;
    private FilterFragment mFilterFg;
    private TagsFragment mTagsFg;
    private FrameLayout mFlView;
    private ViewOperation mViewOperation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        mImgV = (ImageView)findViewById(R.id.img);
        mBackImg = (ImageView)findViewById(R.id.title_img);
        mFlContainer = (FrameLayout)findViewById(R.id.container);
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
        mImgV.setImageBitmap(map);
    }

    private void replaceFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.container, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.addToBackStack(null);
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



}
