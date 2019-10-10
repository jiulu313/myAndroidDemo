package com.zhj.my.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.zhj.my.utils.ViewUtil;

@SuppressLint("AppCompatCustomView")
public class MyButton extends View {
    int lastX;
    int lastY;

    Scroller mScroller;


    public MyButton(Context context) {
        super(context);
        init(context);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            ((View) getParent()).scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    public void smoothScrollTo(int destX, int destY) {
        int scrollX = getScrollX();
        int delta = destX - scrollX;
        //1000秒内滑向destX
        mScroller.startScroll(scrollX, 0, delta, 0, 2000);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //return handleEvent1(event);

        //return handleEvent2(event);

//        return handleEvent3(event);

        return handleEvent4(event);

    }

    //4 scollTo与scollBy, 这两个函数移动的是View的内容
    private boolean handleEvent4(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int offsetX = (int) (event.getX() - lastX);
                int offsetY = (int) (event.getY() - lastY);

                ((View) getParent()).scrollBy(-offsetX, -offsetY);
                break;
        }

        return super.onTouchEvent(event);
    }

    //3 动画方式，具体见ScrollerActivity中 myView的点击事件中的代码
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
