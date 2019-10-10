package com.zhj.my.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.zhj.my.utils.ViewUtil;

@SuppressLint("AppCompatCustomView")
public class MyButton extends View {
    int lastX;
    int lastY;


    public MyButton(Context context) {
        super(context);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.e("zh", "MyButton -> dispatchTouchEvent event=" + ViewUtil.getEventMask(event.getAction()));
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                int x = (int) getX();
                int y = (int) getY();

                int offsetX = x - lastX;
                int offsetY = y - lastY;

                Log.e("zh33","x=" + x + " y=" + y +  " lastX=" + lastX + "  lastY=" + lastY + "  offsetX=" + offsetX + "  offsetY=" + offsetY);

                int left = getLeft() + offsetX;
                int top = getTop() + offsetY;
                int right = left + getWidth();
                int bottom = top + getHeight();

                Log.e("zh22","left=" + left + "  top=" + top + "  right=" + right + "  bottom=" + bottom);

                layout(left, top, right, bottom);
            }
            break;
            case MotionEvent.ACTION_UP:
                break;
        }


        return true;


    }
}
