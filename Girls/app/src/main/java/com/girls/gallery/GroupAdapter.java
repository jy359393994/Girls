package com.girls.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.girls.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gcl on 2015/5/3.
 */
public class GroupAdapter extends BaseAdapter{

    private List<ImageBean> mList = new ArrayList<ImageBean>();
    private Point mPoint = new Point(0,0);//用来封装ImageView的宽和高的对象
    private LayoutInflater mInflater;
    private GridView mGridView;

    public GroupAdapter(Context context,List<ImageBean>list,GridView gridview){
        mList.clear();
        mList.addAll(list);
        mGridView = gridview;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        ImageBean imageBean = mList.get(position);
        String path = imageBean.getTopImagePath();
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.grid_group_item,null);
            viewHolder.mImageView = (MyImageView)convertView.findViewById(R.id.group_image);
            viewHolder.mTextViewTitle = (TextView)convertView.findViewById(R.id.group_title);
            viewHolder.mTextViewCounts = (TextView)convertView.findViewById(R.id.group_count);
            //用来监听ImageView的宽和高
            viewHolder.mImageView.setOnMeasureListener(new MyImageView.OnMeasureListener() {
                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width,height);
                }
            });
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder)convertView.getTag();
            viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }
        viewHolder.mTextViewTitle.setText(imageBean.getFolderName());
        viewHolder.mTextViewCounts.setText(Integer.toString(imageBean.getImageCounts()));
        //给ImageView设置路径Tag,这是异步加载图片的小技巧
        viewHolder.mImageView.setTag(path);

        //利用NativeImageLoader类加载本地图片
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageLoader.NativeImageCallBack() {
            @Override
            public void onImageLoader(Bitmap bitmap, String path) {
                ImageView imageView = (ImageView)mGridView.findViewWithTag(path);
                if(bitmap != null && imageView != null){
                    imageView.setImageBitmap(bitmap);
                }
            }
        });
        if(bitmap != null){
            viewHolder.mImageView.setImageBitmap(bitmap);
        }else{
            viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }

        return convertView;
    }


    public static class ViewHolder{
        public MyImageView mImageView;
        public TextView mTextViewTitle;
        public TextView mTextViewCounts;
    }
}
