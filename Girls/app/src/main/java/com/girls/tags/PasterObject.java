package com.girls.tags;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;

import com.girls.R;
import com.girls.event.DisplayEvent;
import com.girls.event.HideEvent;

import event.EventBus;

/**
 * @author gcl
 * @date 2015/6/17 13:19
 * @description
 */
public class PasterObject implements ObjectDrawable{

    private final int NONE = 0;
    private final int DRAG = 1;
    private final int ZOOM_SCALE = 2;
    private final int ZOOM_SCALE_BY_ICON = 3;
    private int mode = NONE;
    private PaintFlagsDrawFilter mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
    private ObjectInfo mInfo = new ObjectInfo();
    private CanvasViewObserver mCanvasViewObserver;
    private Matrix mMatrixDraw = new Matrix();
    private Matrix mMatrixTemp = new Matrix();
    private Matrix mMatrixSaved = new Matrix();
    private float mDownX = 0;
    private float mDownY = 0;
    private float mOldDist = 0;
    private float mOldRotation = 0;
    private boolean mIsSelect = false;
    private Context mContext;
    private Bitmap mBitmap;
    private Paint mPaintOutline;
    private Bitmap mBpIconDel;
    private Bitmap mBpIconMirro;
    private Bitmap mBpIconControl;
    private int mIconWidth;
    private int mIconHeight;
    private PointF mRotateScaleCenterPoint = new PointF();
    private float mDisX;
    private float mDisY;
    private DelObjectDrawableListener mDelListener;

    public PasterObject(Context context,Bitmap bitmap){

        mMatrixDraw.reset();
        mMatrixTemp.reset();
        mMatrixSaved.reset();
        mContext = context;
        mBitmap = bitmap;
        mBpIconDel = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.edit_del);
        mBpIconMirro = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.edit_symmetry);
        mBpIconControl = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.edit_control);
        mInfo.mRawCorner1 = new PointF(0.0f,0.0f);
        mInfo.mRawCorner2 = new PointF(mBitmap.getWidth(),0.0f);
        mInfo.mRawCorner3 = new PointF(mBitmap.getWidth(),mBitmap.getHeight());
        mInfo.mRawCorner4 = new PointF(0.0f,mBitmap.getHeight());
        RectF rect = new RectF(0,0,mBitmap.getWidth(),mBitmap.getHeight());
        mInfo.mCenter = new PointF(rect.centerX(),rect.centerY());
        mIconWidth = mBpIconDel.getWidth();
        mIconHeight = mBpIconDel.getHeight();
        mPaintOutline = new Paint();
        mPaintOutline.setColor(Color.parseColor("#FFE7513D"));
        mPaintOutline.setStrokeWidth(2);

    }

    public interface DelObjectDrawableListener{
        void toDelObjectDrawable(ObjectDrawable obj);
    }
    public void setDelObjectDrawableListener(DelObjectDrawableListener l){
        mDelListener = l;
    }


    public void setInitStatus(Matrix matrix,boolean isMirror){
        if(isMirror){
            mBitmap = createYMirrorBitmap(mBitmap);
        }
        mMatrixDraw.set(matrix);
        mMatrixTemp.set(matrix);
        setObjectInfo();
    }



    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.setDrawFilter(mDrawFilter);
        canvas.drawBitmap(mBitmap, mMatrixDraw, null);

        if(mIsSelect){
            canvas.drawLine(mInfo.mRawCorner1.x,mInfo.mRawCorner1.y,
                    mInfo.mRawCorner2.x,mInfo.mRawCorner2.y,mPaintOutline);
            canvas.drawLine(mInfo.mRawCorner2.x,mInfo.mRawCorner2.y,
                    mInfo.mRawCorner3.x,mInfo.mRawCorner3.y,mPaintOutline);
            canvas.drawLine(mInfo.mRawCorner3.x,mInfo.mRawCorner3.y,
                    mInfo.mRawCorner4.x,mInfo.mRawCorner4.y,mPaintOutline);
            canvas.drawLine(mInfo.mRawCorner4.x,mInfo.mRawCorner4.y,
                    mInfo.mRawCorner1.x,mInfo.mRawCorner1.y,mPaintOutline);
            canvas.drawBitmap(mBpIconDel, mInfo.mRawCorner1.x - mIconWidth / 2,
                    mInfo.mRawCorner1.y - mIconWidth / 2, null);
            canvas.drawBitmap(mBpIconMirro,mInfo.mRawCorner2.x - mIconWidth/2,
                    mInfo.mRawCorner2.y-mIconWidth/2,null);
            canvas.drawBitmap(mBpIconControl,mInfo.mRawCorner3.x - mIconWidth/2,
                    mInfo.mRawCorner3.y - mIconWidth/2,null);
        }
        canvas.restore();
    }

    @Override
    public boolean isContainPoint(PointF point) {
        float[] mappoint = new float[2];
        Matrix matrix = new Matrix();
        matrix.set(mMatrixDraw);
        boolean isCan = matrix.invert(matrix);
        if(!isCan){
            Log.i("PasterObject","can not revert matrix");
            return false;
        }
        matrix.mapPoints(mappoint, new float[]{point.x, point.y});
        boolean isInBitmap = mappoint[0] > 0 && mappoint[0] < mBitmap.getWidth()
                && mappoint[1] > 0 && mappoint[1] < mBitmap.getHeight();
        boolean isInControlIcon = isInControlIcon(point.x,point.y) != 0;
        if(isInBitmap || isInControlIcon){
            return true;
        }
        return false;
    }

    @Override
    public boolean isToHide(PointF point) {
        float[] mappoint = new float[2];
        Matrix matrix = new Matrix();
        matrix.set(mMatrixDraw);
        boolean isCan = matrix.invert(matrix);
        if(!isCan){
            Log.i("PasterObject","can not revert matrix");
            return false;
        }
        matrix.mapPoints(mappoint,new float[]{point.x,point.y});
        boolean isInBitmap = mappoint[0] > 0 && mappoint[0] < mBitmap.getWidth()
                && mappoint[1] > 0 && mappoint[1] < mBitmap.getHeight();
        boolean isInControlIcon = isInControlIcon(point.x,point.y) == 3;
        if(isInBitmap || isInControlIcon){
            return true;
        }
        return false;
    }

    @Override
    public boolean onEvent(MotionEvent event) {
        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                mDisX = mDownX;
                mDisY = mDownY;
                mMatrixSaved.set(mMatrixDraw);
//                mMatrixTemp.set(mMatrixDraw);
                if(isInControlIcon(event.getX(),event.getY()) == 1){
                    //删除贴纸
                    if(mDelListener != null){
                        mDelListener.toDelObjectDrawable(this);
                    }
                }
                else if(isInControlIcon(event.getX(),event.getY()) == 2){
                    //y轴镜像
                    mBitmap = createYMirrorBitmap(mBitmap);
                    mCanvasViewObserver.onReDraw();
                }
                else if(isInControlIcon(event.getX(),event.getY()) == 3){
 //                   EventBus.getDefault().post(new HideEvent());
                    //控制图像
                    mode = ZOOM_SCALE_BY_ICON;
                    mOldDist = spacingTwoPoint(mInfo.mCenter.x, mInfo.mCenter.y,
                            event.getX(), event.getY());
                    mOldRotation = ratoteByControlPoint(event);
                    mRotateScaleCenterPoint.set(mInfo.mCenter.x,mInfo.mCenter.y);
//                    mMatrixTemp.set(mMatrixDraw);
                    mMatrixSaved.set(mMatrixDraw);
                }
                else{
//                    EventBus.getDefault().post(new HideEvent());
                    mode = DRAG;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM_SCALE;
 //               EventBus.getDefault().post(new HideEvent());
                mOldDist = spacingTwoPoint(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                mOldRotation = ratoteByTwoFinger(event);
                mMatrixSaved.set(mMatrixDraw);
                mRotateScaleCenterPoint.set(mInfo.mCenter.x,mInfo.mCenter.y);
                break;
            case MotionEvent.ACTION_MOVE:
                if(mode == ZOOM_SCALE){
                    mMatrixTemp.set(mMatrixSaved);
                    float newdist = spacingTwoPoint(event.getX(0),event.getY(0),event.getX(1),event.getY(1));
                    float scale = newdist / mOldDist;
                    float rotation = ratoteByTwoFinger(event) - mOldRotation;
                    mMatrixTemp.postScale(scale,scale,mRotateScaleCenterPoint.x,mRotateScaleCenterPoint.y);
                    mMatrixTemp.postRotate(rotation, mRotateScaleCenterPoint.x, mRotateScaleCenterPoint.y);
                    mMatrixDraw.set(mMatrixTemp);
                    mCanvasViewObserver.onReDraw();
                    setObjectInfo();

                }
                else if(mode == ZOOM_SCALE_BY_ICON){
                    mMatrixTemp.set(mMatrixSaved);
                    float newdist = spacingTwoPoint(mInfo.mCenter.x,mInfo.mCenter.y,
                            event.getX(),event.getY());
                    float scale = newdist / mOldDist;
                    float rotation = ratoteByControlPoint(event) - mOldRotation;
                    mMatrixTemp.postScale(scale,scale,mRotateScaleCenterPoint.x,mRotateScaleCenterPoint.y);
                    mMatrixTemp.postRotate(rotation, mRotateScaleCenterPoint.x, mRotateScaleCenterPoint.y);
                    mMatrixDraw.set(mMatrixTemp);
                    mCanvasViewObserver.onReDraw();
                    setObjectInfo();

                }
                else if(mode == DRAG){
                    mMatrixTemp.set(mMatrixSaved);
//                    mMatrixTemp.set(mMatrixDraw);
                    mMatrixTemp.postTranslate(event.getX() - mDownX, event.getY() - mDownY);
//                    mMatrixTemp.postTranslate(event.getX() - mDisX, event.getY() - mDisY);
                    mMatrixDraw.set(mMatrixTemp);
                    mCanvasViewObserver.onReDraw();
/*                    mDisX = event.getX();
                    mDisY = event.getY();*/
                    setObjectInfo();

                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
//                EventBus.getDefault().post(new DisplayEvent());
                mode = NONE;
                break;
        }
        return true;
    }

    @Override
    public void setOnRedrawListener(CanvasViewObserver listener) {
        mCanvasViewObserver = listener;
    }

    @Override
    public void setIsSelected(boolean isSelect) {
        mIsSelect = isSelect;
    }

    /**
     * 贴纸的初始位置中，删除Icon在第一个点，镜像Icon在第二个点，contral Icon在第三个点
     * @param x
     * @param y
     * @return
     */
    private int isInControlIcon(float x,float y){
        if(spacingTwoPoint(x,y,mInfo.mRawCorner1.x,mInfo.mRawCorner1.y) < mIconWidth/2){
            return 1;
        }
        if(spacingTwoPoint(x,y,mInfo.mRawCorner2.x,mInfo.mRawCorner2.y) < mIconWidth/2){
            return 2;
        }
        if(spacingTwoPoint(x,y,mInfo.mRawCorner3.x,mInfo.mRawCorner3.y) < mIconWidth/2){
            return 3;
        }
        return 0;
    }

    private float spacingTwoPoint(float x1,float y1,float x2,float y2){
        return FloatMath.sqrt((x1-x2) * (x1-x2)  + (y1 - y2) * (y1 - y2));
    }

    private void setObjectInfo(){
        float[] f = new float[9];
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        mMatrixDraw.getValues(f);
        mInfo.mRawCorner1.x = f[0] * 0 + f[1] * 0 + f[2];
        mInfo.mRawCorner1.y = f[3] * 0 + f[4] * 0 + f[5];

        mInfo.mRawCorner2.x = f[0] * width + f[1] * 0 + f[2];
        mInfo.mRawCorner2.y = f[3] * width + f[1] * 0 + f[5];

        mInfo.mRawCorner3.x = f[0] * width + f[1] * height + f[2];
        mInfo.mRawCorner3.y = f[3] * width + f[4] * height + f[5];

        mInfo.mRawCorner4.x = f[0] * 0 + f[1] * height + f[2];
        mInfo.mRawCorner4.y = f[3] * 0 + f[4] * height + f[5];

        RectF srcf = new RectF(0,0,width,height);
        RectF dstf = new RectF();
        mMatrixDraw.mapRect(dstf,srcf);
        mInfo.mCenter.x = dstf.centerX();
        mInfo.mCenter.y = dstf.centerY();
    }

    private Bitmap createYMirrorBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();
        float[] f = {-1.0f,0.0f,0.0f,0.0f,1.0f,0.0f,0.0f,0.0f,1.0f};
        matrix.setValues(f);
        Bitmap b = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        bitmap.recycle();
        System.gc();
        return b;
    }

    private Bitmap createXMirrorBitmap(Bitmap bitmap){
        Matrix matrix = new Matrix();
        float[] f = {1.0f,0.0f,0.0f,0.0f,-1.0f,0.0f,0.0f,0.0f,1.0f};
        matrix.setValues(f);
        Bitmap b = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
        bitmap.recycle();
        System.gc();
        return b;
    }

    private float ratoteByTwoFinger(MotionEvent event){
        double x = event.getX(0) - event.getX(1);
        double y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(y,x);
        return (float)Math.toDegrees(radians);
    }

    private float ratoteByControlPoint(MotionEvent event){
        double x = event.getX() - mInfo.mCenter.x;
        double y = event.getY() - mInfo.mCenter.y;
        double radians = Math.atan2(y,x);
        return (float)Math.toDegrees(radians);
    }



}
