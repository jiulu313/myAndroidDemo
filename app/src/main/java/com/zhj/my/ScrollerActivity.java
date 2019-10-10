package com.zhj.my;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

public class ScrollerActivity extends Activity {
    View myView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroller);


        myView = findViewById(R.id.myButton);
        myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //3 动画方式移动view,下面两种动画方式都可以
                ObjectAnimator.ofFloat(myView,"translationX",0,300).setDuration(1000).start();
                myView.startAnimation(AnimationUtils.loadAnimation(ScrollerActivity.this, R.anim.translate));
            }
        });

    }
}
