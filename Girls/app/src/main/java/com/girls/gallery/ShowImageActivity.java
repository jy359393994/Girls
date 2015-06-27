package com.girls.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.girls.activity.EditActivity;
import com.girls.R;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Administrator on 2015/5/4.
 */
public class ShowImageActivity extends Activity implements View.OnClickListener {
    private GridView mGridView;
    private List<String> mList = new ArrayList<String>();
    private ChildAdapter mAdapter;
    private LinearLayout mNextLl;
    private ImageView mBackImg;
    private LinkedList<ChildAdapter.SelectItem> mSelectItems = new LinkedList<ChildAdapter.SelectItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image_activity);
        mGridView = (GridView)findViewById(R.id.child_grid);
        mList.clear();
        mList.addAll(getIntent().getStringArrayListExtra("data"));
        mAdapter = new ChildAdapter(this,mList,mGridView,1);
        mGridView.setAdapter(mAdapter);
        mNextLl = (LinearLayout)findViewById(R.id.layout_next);
        mBackImg = (ImageView)findViewById(R.id.title_img);
        mBackImg.setOnClickListener(this);
        mNextLl.setOnClickListener(this);
    }
/*
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "选中 " + mAdapter.getSelectItems().size() + " item", Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }
    */

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_next:
                mSelectItems.clear();
                mSelectItems.addAll( mAdapter.getmSelectItems());
                if(mSelectItems.size() == 1){
                    Intent it = new Intent(this,EditActivity.class);
                    Uri uri = getOutputMediaFileUri(new File(mSelectItems.get(0).path));
                    it.putExtra("uri",uri);
                    startActivity(it);
                }
                break;
            case R.id.title_img:
                finish();
                break;
        }
    }

    private Uri getOutputMediaFileUri(File file)
    {
        return Uri.fromFile(file);
    }
}
