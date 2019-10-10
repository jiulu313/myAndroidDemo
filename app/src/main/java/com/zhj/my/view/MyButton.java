package com.zhj.my.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

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

        //return handleEvent1(event);

        //return handleEvent2(event);

        return handleEvent3(event);

    }

    private boolean handleEvent3(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    //2 LayoutParams（改变布局参数）
    private boolean handleEvent2(MotionEvent event) {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) getLayoutParams();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = (int) (event.getX() - lastX);
                int offsetY = (int) (event.getY() - lastY);

                lp.leftMargin = getLeft() + offsetX;
                lp.topMargin = getTop() + offsetY;

                setLayoutParams(lp);
                break;
        }


        return true;
    }

    //1 使用layout()方法来对自己重新布局，从而达到移动View的效果
    private boolean handleEvent1(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            }
            case MotionEvent.ACTION_MOVE: {

                int x = (int) event.getX();
                int y = (int) event.getY();

                int offsetX = x - lastX;
                int offsetY = y - lastY;

                Log.e("zh33", "x=" + x + " y=" + y + " lastX=" + lastX + "  lastY=" + lastY + "  offsetX=" + offsetX + "  offsetY=" + offsetY);

                int left = getLeft() + offsetX;
                int top = getTop() + offsetY;
                int right = getRight() + offsetX;
                int bottom = getBottom() + offsetY;

                Log.e("zh22", "left=" + left + "  top=" + top + "  right=" + right + "  bottom=" + bottom);

//                layout(left, top, right, bottom);

                offsetLeftAndRight(offsetX);
                //对top和bottom进行偏移
                offsetTopAndBottom(offsetY);
            }
            break;
            case MotionEvent.ACTION_UP:
                break;
        }


        return true;
    }
}
