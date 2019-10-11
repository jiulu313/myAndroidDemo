package com.zhj.my.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.zhj.my.R;

public class MyDrawView extends View {
    Paint paint = new Paint();

    public MyDrawView(Context context) {
        this(context,null);
    }

    public MyDrawView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyDrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {

    }

    Camera camera = new Camera();

//    @Override
//    protected void onDraw(Canvas canvas) {
////        canvas.drawColor(Color.parseColor("#ff2233"));
//        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//
//        canvas.save();
//        camera.rotateY(50); // 旋转 Camera 的三维空间
//        camera.applyToCanvas(canvas); // 把旋转投影到 Canvas
//        canvas.drawBitmap(bitmap, 0, 0, paint);
//        canvas.restore();
//
//
//    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        Log.e("zh99","MyDrawView width=" + width + "  height=" + height);

    }
}
