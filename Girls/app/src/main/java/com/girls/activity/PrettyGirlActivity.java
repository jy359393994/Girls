package com.girls.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.girls.R;

import java.io.File;
import java.io.IOException;


public class PrettyGirlActivity extends BaseActivity implements View.OnClickListener {

    private FloatingActionButton mActCamera;
    private FloatingActionButton mActGallery;
    private String DIR = Environment.getExternalStorageDirectory().getPath() + "/Girls/";
    private static int CAMERA_REQUET_CODE = 102;
    private static int GALLERY_REQUET_CODE = 103;
    private ImageView mTest;
    private Uri mFileUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prettygirl);
        mActCamera = (FloatingActionButton)findViewById(R.id.action_camera);
        mActGallery = (FloatingActionButton)findViewById(R.id.action_gallery);
        mTest = (ImageView)findViewById(R.id.test);
        mActCamera.setOnClickListener(this);
        mActGallery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent it = null;
        switch(v.getId()){
            case R.id.action_camera:
                it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mFileUri = getOutputMediaFileUri();
                it.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
                startActivityForResult(it,CAMERA_REQUET_CODE);
                break;
            case R.id.action_gallery:
                Intent localIntent2 = new Intent(this,MyGalleryActivity.class);
                startActivity(localIntent2);

//                localIntent2.setType("image/*");
//                localIntent2.setAction("android.intent.action.GET_CONTENT");
//                startActivityForResult(Intent.createChooser(localIntent2, "选择图片"),
//                        GALLERY_REQUET_CODE);

//                it = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                break;
        }
    }

    private  Uri getOutputMediaFileUri()
    {
        return Uri.fromFile(getOutMediaFile());
    }

    private  File getOutMediaFile(){
        String sdStatus = Environment.getExternalStorageState();
        if(!sdStatus.equals(Environment.MEDIA_MOUNTED)){
            return null;
        }
        String name = System.currentTimeMillis() +  ".png";
        String path = Environment.getExternalStorageDirectory().getPath() + "/Girls/" + name;
        File dir = new File(DIR);
        File file = new File(DIR + name);
        if(!dir.isDirectory()) {
            dir.mkdirs();
        }
        if(!file.exists()) {
           try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        return file;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQUET_CODE){
            if(RESULT_OK == resultCode){
     //           if(data != null){
                    Intent it = new Intent(this,EditActivity.class);
                    it.putExtra("uri",mFileUri);
                    startActivity(it);
//                    String sdStatus = Environment.getExternalStorageState();
//                    if(!sdStatus.equals(Environment.MEDIA_MOUNTED)){
//                        return;
//                    }
//                    String name = System.currentTimeMillis() +  ".png";
//                    String path = Environment.getExternalStorageDirectory().getPath() + "/Girls/" + name;
//                    File dir = new File(DIR);
//                    File file = new File(DIR + name);
//                    if(!dir.isDirectory()){
//                        dir.mkdirs();
//                        if(!file.exists()) {
//                            try {
//                                file.createNewFile();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    FileOutputStream out = null;
//                           try {
//                               out =  new FileOutputStream(file.getPath());
//                               thumbnail.compress(Bitmap.CompressFormat.PNG, 100, out);// °ÑÊý¾ÝÐ´ÈëÎÄ¼þ
//                           }catch (FileNotFoundException e){
//                               e.printStackTrace();
//                           }finally {
//                               try {
//                                   if(out != null) {
//                                       out.flush();
//                                       out.close();
//                                   }
//                                   it.putExtra("path",path);
//                                   startActivity(it);
//                               }catch(IOException e){
//                                   e.printStackTrace();
//                               }
//                           }

      //              mTest.setImageBitmap(thumbnail);
 //               }
            }
        }

    }
}
