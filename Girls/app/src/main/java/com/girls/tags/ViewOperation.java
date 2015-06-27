package com.girls.tags;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;

import com.girls.event.DisplayEvent;
import com.girls.event.HideEvent;
import com.girls.tools.AndroidTools;

import java.util.ArrayList;
import java.util.List;

import event.EventBus;

/**
 * @author gcl
 * @date 2015/6/17. 22:24
 * @description
 */
public class ViewOperation extends View implements  CanvasViewObserver,PasterObject.DelObjectDrawableListener {


    private List<ObjectDrawable> mListObjectDrawable = new ArrayList<ObjectDrawable>();
    private int mCurSelect = -1;
    private PointF mTouchDownPoint = new PointF();
    private Context mContext;
    private TouchType mTouchType = TouchType.NONE;
    private enum TouchType{
        NONE,
        DOWN,
        UP
    }




    public ViewOperation(Context context) {

        super(context);
        mContext = context;
    }

    public ViewOperation(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ViewOperation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for(int i = 0;i < mListObjectDrawable.size();i++){
            mListObjectDrawable.get(i).draw(canvas);
        }
    }

    public void addObjectDrawable(ObjectDrawable obj){
        if(obj instanceof PasterObject){
            ((PasterObject)obj).setDelObjectDrawableListener(this);
        }
        mListObjectDrawable.add(obj);
        invalidate();
    }
    public int getObjectSize(){
        return mListObjectDrawable.size();
    }

    public void removeObjectDrawable(ObjectDrawable obj){
        mListObjectDrawable.remove(obj);
        invalidate();
    }
    @Override
    public void onReDraw() {
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mTouchType = TouchType.DOWN;
                mTouchDownPoint.set(event.getX(),event.getY());
                if(isClickIn(mTouchDownPoint) && mCurSelect != -1){
                    EventBus.getDefault().post(new HideEvent());
                }
                break;

            case MotionEvent.ACTION_MOVE:

                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mTouchType = TouchType.UP;
                float space = spacingTwoPoint(mTouchDownPoint.x,mTouchDownPoint.y,
                        event.getX(),event.getY());
                if(space < AndroidTools.dip2px(mContext, 2.0f)){
                    clickEvent(mTouchDownPoint);
                }
                EventBus.getDefault().post(new DisplayEvent());
                mTouchDownPoint.set(0,0);
                break;
        }
        if(mCurSelect != -1){
//            EventBus.getDefault().post(new HideEvent());
            return mListObjectDrawable.get(mCurSelect).onEvent(event);
        }
        if(mTouchType.equals(TouchType.UP)){
//            EventBus.getDefault().post(new DisplayEvent());
        }

        return true;

    }

    private boolean isClickIn(PointF clickPoint){
        float x = clickPoint.x;
        float y = clickPoint.y;
        int n = mListObjectDrawable.size() - 1;
        for(int i = n;i >= 0;i--){
            if(mListObjectDrawable.get(i).isToHide(new PointF(x,y))){
                return true;
            }
        }
        return false;
    }

    private boolean clickEvent(PointF clickPoint){
        int selectIndex = -1;
        float x = clickPoint.x;
        float y = clickPoint.y;
        int n = mListObjectDrawable.size() - 1;
        for(int i = n;i >= 0;i--){
            if(mListObjectDrawable.get(i).isContainPoint(new PointF(x,y))){
                selectIndex = i;
                break;
            }
        }
        if(selectIndex != -1){
            if(mCurSelect != -1) {
                if(mListObjectDrawable.size() > mCurSelect)
                    mListObjectDrawable.get(mCurSelect).setIsSelected(false);
            }
            mListObjectDrawable.get(selectIndex).setIsSelected(true);
            mCurSelect = selectIndex;
            invalidate();
            return true;
        }
        if(mCurSelect != -1) {
            if(mListObjectDrawable.size() > mCurSelect) {
                mListObjectDrawable.get(mCurSelect).setIsSelected(false);
            }
        }
        mCurSelect = -1;
        invalidate();
        return false;

    }
    private float spacingTwoPoint(float x1,float y1,float x2,float y2){
        return FloatMath.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
    }

    @Override
    public void toDelObjectDrawable(ObjectDrawable obj) {
        removeObjectDrawable(obj);
    }

}
