package com.zhj.my.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.zhj.my.utils.ViewUtil;

public class MyScrollView extends ScrollView {
    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.e("zh","MyScrollView -> onTouchEvent event=" + ViewUtil.getEventMask(event.getAction()));
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e("zh","MyScrollView -> dispatchTouchEvent event=" + ViewUtil.getEventMask(ev.getAction()));
        return super.dispatchTouchEvent(ev);
    }
}
