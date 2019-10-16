package com.zhj.my;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.zhj.my.view.MyEditText;

public class MainActivity extends Activity implements View.OnClickListener {

//    MyEditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnScroller).setOnClickListener(this);
        findViewById(R.id.btnDraw).setOnClickListener(this);
//        editText = findViewById(R.id.myEditText);
//        editText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                Log.e("dd33","beforeTextChanged s=" + s.toString());
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                Log.e("dd33","onTextChanged s=" + s.toString());
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                Log.e("dd33","afterTextChanged s=" + s.toString());
//            }
//        });

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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int keyCode = event.getKeyCode();
        int deviceId = event.getDeviceId();
        Log.e("dd337","dispatchKeyEvent == " + keyCode + "  deviceId=" + deviceId);



        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER){
            Log.e("dd33","KEYCODE_ENTER 发生了");
        }


        Log.e("dd33","onKeyDown == " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public View getCurrentFocus() {
        Log.e("dd33","getCurrentFocus 执行了 " );
        return super.getCurrentFocus();
    }


}
