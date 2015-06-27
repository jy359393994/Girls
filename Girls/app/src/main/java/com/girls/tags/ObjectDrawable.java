package com.girls.tags;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.view.MotionEvent;

/**
 * @author gcl
 * @date 2015/6/17 11:49
 * @description
 */
public interface ObjectDrawable {

    void draw(Canvas canvas);
    boolean isContainPoint(PointF point);
    boolean onEvent(MotionEvent event);
    void setOnRedrawListener(CanvasViewObserver listener);
    void setIsSelected(boolean isSelect);
    boolean isToHide(PointF point);

}
