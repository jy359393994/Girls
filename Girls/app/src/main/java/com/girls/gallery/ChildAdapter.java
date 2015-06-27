package com.girls.gallery;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.girls.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by gcl on 2015/5/3.
 */
public class ChildAdapter extends BaseAdapter{

    private Point mPoint = new Point(0,0);//用来封装ImageView的宽和高的对象

    /**
     *用来存储图片的选中情况
     */
    private Map<Integer,Boolean> mSelectMap = new HashMap<Integer,Boolean>();
    private GridView mGridView;
    private List<String> mList = new ArrayList<String>();
    private LayoutInflater mInflater;

    private int mMaxSelectCount;
    private LinkedList<SelectItem>  mSelectItems= new  LinkedList<SelectItem>();
    private Context mContext;
//    private List<String> mSelectItemPath = new ArrayList<String>(9);


    public ChildAdapter(Context context,List<String>list,GridView gridview,int maxcount){
        mList.clear();
        mList.addAll(list);
        mGridView = gridview;
        mInflater = LayoutInflater.from(context);
        mMaxSelectCount = maxcount;
        mContext = context;
    }


    @Override
    public int getCount() {

        return mList.size();
    }

    @Override
    public Object getItem(int position){

        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        String path = mList.get(position);
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.grid_child_item,null);
            viewHolder = new ViewHolder();
            viewHolder.mImageView = (MyImageView)convertView.findViewById(R.id.child_image);

            viewHolder.mIsSelect = false;
//            viewHolder.mCheckBox = (CheckBox)convertView.findViewById(R.id.child_checkbox);
            //用来监听ImageView的宽和高
            viewHolder.mImageView.setOnMeasureListener(new MyImageView.OnMeasureListener() {
                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width,height);
                }
            });
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
            viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }
        viewHolder.mViewLayout = (FrameLayout)convertView.findViewById(R.id.frameLayout);
//        layout.setSelected(false);
        viewHolder.mViewLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewHolder.mIsSelect = !viewHolder.mIsSelect;
                mSelectMap.put(position,viewHolder.mIsSelect);
                if(viewHolder.mIsSelect){
                    if(mSelectItems.size() >= mMaxSelectCount){
                        viewHolder.mIsSelect = !viewHolder.mIsSelect;
                        view.setSelected(false);
                        mSelectMap.put(position,viewHolder.mIsSelect);
                        Toast.makeText(mContext,"最多选择" + mMaxSelectCount + "张图片",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        mSelectItems.add(new SelectItem( mList.get(position),position));
                        view.setSelected(true);
                    }
                }
                else{
                    view.setSelected(false);
                    for(SelectItem item:mSelectItems){
                        if(item.pos == position){
                            mSelectItems.remove(item);
                            break;
                        }
                    }
                }
            }
        });
        viewHolder.mImageView.setTag(path);
        /*
        viewHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //如果是未选中的checkbox，则添加动画
                if(!mSelectMap.containsKey(position) || !mSelectMap.get(position)){
                    addAnimation(viewHolder.mCheckBox);
                }
                mSelectMap.put(position,b);
            }
        });*/
/*        if(viewHolder.mIsSelect){
            layout.setSelected(true);
        }
        else{
            layout.setSelected(false);
        }*/
//        layout.setSelected(mSelectMap.containsKey(position)?mSelectMap.get(position):false);

        //利用NativeImageLoader类加载本地图片
        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path,mPoint,new NativeImageLoader.NativeImageCallBack() {
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
        }
        else{
            viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }
        return convertView;
    }

    /**
     * 获取到用户选择的item项
     * @return
     */
    public LinkedList<SelectItem> getmSelectItems(){
        return mSelectItems;
    }

    /**
     * 给CheckBox加点击动画
     * @param view
     */
    private  void addAnimation(View view){
        float[] values = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", values),
                ObjectAnimator.ofFloat(view, "scaleY", values));
        set.setDuration(150);
        set.start();
    }

    /**
     * 获取选中的Item的position
     * @return
     */
    public List<Integer>getSelectItems(){
        List<Integer>list = new ArrayList<Integer>();
        for(Iterator<Map.Entry<Integer,Boolean>>it = mSelectMap.entrySet().iterator();it.hasNext();){
            Map.Entry<Integer,Boolean>entry = it.next();
            if(entry.getValue()){
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public List<String>getSelectItemsPath(){
        List<String>pathList = new ArrayList<String>();
        List<Integer>list = new ArrayList<Integer>();
        for(Iterator<Map.Entry<Integer,Boolean>>it = mSelectMap.entrySet().iterator();it.hasNext();){
            Map.Entry<Integer,Boolean>entry = it.next();
            if(entry.getValue()){
                list.add(entry.getKey());
            }
        }
        for(int i=0;i<list.size();i++){
            pathList.add(mList.get(list.get(i)));
        }
        return pathList;
    }

    private  class ViewHolder{
        public MyImageView mImageView;
        public Boolean mIsSelect;
        public FrameLayout mViewLayout;
    }

    public static class SelectItem{
        public String path;
        public int pos;
        public SelectItem(String path,int pos){
            this.path = path;
            this.pos = pos;
        }
    }
}
