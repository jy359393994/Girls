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
                    //�رս�����
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
     * ����ContentProviderɨ���ֻ��е�ͼƬ���˷������������߳���
     */

    private void getImages(){
        if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Toast.makeText(this,"�����ⲿ����",Toast.LENGTH_SHORT).show();
            return;
        }
        //��ʾ������
        mProgressDialog = ProgressDialog.show(this,null,"���ڼ���...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver contentResolver = MyGalleryActivity.this.getContentResolver();
                //��ѯjpeg��png��ͼƬ
                Cursor cursor = contentResolver.query(imageUri,null,MediaStore.Images.Media.MIME_TYPE + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                       new String[]{"image/jpeg","image/png"},MediaStore.Images.Media.DATE_MODIFIED );
                while(cursor.moveToNext()){
                    //��ȡ��ͼƬ��·��
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                    //��ȡ��ͼƬ�ĸ�·����
                    String parentName = new File(path).getParentFile().getName();

                    //���ݸ�·������ͼƬ���뵽mGroupMap��
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
                //֪ͨHandlerɨ��ͼƬ���
                mHandler.sendEmptyMessage(SCAN_OK);
            }
        }).start();
    }



    /**
     * ��װ�������GridView������Դ����Ϊ����ɨ���ֻ���ʱ��ͼƬ��Ϣ����HashMap��
     * ������Ҫ����HashMap��������װ��list
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
            imageBean.setTopImagePath(value.get(0));//��ȡ����ĵ�һ��ͼƬ
            list.add(imageBean);
        }
        return list;
    }



}
