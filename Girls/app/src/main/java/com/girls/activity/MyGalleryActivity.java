package com.girls.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.girls.R;
import com.girls.gallery.GroupAdapter;
import com.girls.gallery.ImageBean;
import com.girls.gallery.ShowImageActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by gcl on 2015/4/29.
 */
public class MyGalleryActivity extends BaseActivity{
    private Map<String,List<String>>mGroupMap = new HashMap<String,List<String>>();
    private List<ImageBean>mList = new ArrayList<ImageBean>();
    private final static int SCAN_OK = 1;
    private ProgressDialog mProgressDialog;
    private GridView mGroupGridView;
    private GroupAdapter mGroupAdapter;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SCAN_OK:
                    //关闭进度条
                    mProgressDialog.dismiss();
                    mGroupAdapter = new GroupAdapter(MyGalleryActivity.this,mList=subGroupOfImage(mGroupMap),mGroupGridView);
                    mGroupGridView.setAdapter(mGroupAdapter);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        mGroupGridView = (GridView)findViewById(R.id.main_grid);
        getImages();
        mGroupGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<String>childList = mGroupMap.get(mList.get(i).getFolderName());
                Intent it = new Intent(MyGalleryActivity.this, ShowImageActivity.class);
                it.putStringArrayListExtra("data",(ArrayList<String>)childList);
                startActivity(it);
            }
        });
    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法运行在子线程中
     */

    private void getImages(){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this,"暂无外部储存",Toast.LENGTH_SHORT).show();
            return;
        }
        //显示进度条
        mProgressDialog = ProgressDialog.show(this,null,"正在加载...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = MyGalleryActivity.this.getContentResolver();
                //查询jpeg和png的图片
                Cursor cursor = contentResolver.query(imageUri,null,MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                       new String[]{"image/jpeg","image/png"},MediaStore.Images.Media.DATE_MODIFIED );
                while(cursor.moveToNext()){
                    //获取该图片的路径
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    //获取该图片的父路径名
                    String parentName = new File(path).getParentFile().getName();

                    //根据父路径名将图片放入到mGroupMap中
                    if(!mGroupMap.containsKey(parentName)){
                        List<String>childList = new ArrayList<String>();
                        childList.add(path);
                        mGroupMap.put(parentName,childList);
                    }
                    else{
                        mGroupMap.get(parentName).add(path);
                    }
                }
                cursor.close();
                //通知Handler扫描图片完成
                mHandler.sendEmptyMessage(SCAN_OK);
            }
        }).start();
    }



    /**
     * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
     * 所以需要遍历HashMap将数据组装成list
     * @param groupMap
     * @return
     */
    private List<ImageBean> subGroupOfImage(Map<String,List<String>> groupMap){
        if(groupMap.size() == 0){
            return null;
        }
        List<ImageBean>list = new ArrayList<ImageBean>();
        Iterator<Map.Entry<String,List<String>>> it = groupMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<String,List<String>>entry = it.next();
            ImageBean imageBean = new ImageBean();
            String key = entry.getKey();
            List<String>value = entry.getValue();
            imageBean.setFolderName(key);
            imageBean.setImageCounts(value.size());
            imageBean.setTopImagePath(value.get(0));//获取该组的第一张图片
            list.add(imageBean);
        }
        return list;
    }



}
