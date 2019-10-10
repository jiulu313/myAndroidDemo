package com.zhj.my.utils;

import android.view.MotionEvent;

public class ViewUtil {

    public static String getEventMask(int action){
        if(action == MotionEvent.ACTION_DOWN){
            return "ACTION_DOWN";
        }else if(action == MotionEvent.ACTION_UP){
            return "ACTION_UP";
        }else if(action == MotionEvent.ACTION_MOVE){
            return "ACTION_MOVE";
        }else if(action == MotionEvent.ACTION_CANCEL){
            return "ACTION_CANCEL";
        }else {
            return "ACTION_" + action;
        }
    }

}
