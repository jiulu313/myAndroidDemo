package com.zhj.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnScroller).setOnClickListener(this);
        findViewById(R.id.btnDraw).setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            case R.id.btnScroller:
                intent.setClass(MainActivity.this, ScrollerActivity.class);
                startActivity(intent);
                break;
            case R.id.btnDraw:
                intent.setClass(MainActivity.this, DrawActivity.class);
                startActivity(intent);
                break;
        }
    }
}
