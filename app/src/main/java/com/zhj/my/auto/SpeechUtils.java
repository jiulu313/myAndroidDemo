package com.zhj.my.auto;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class SpeechUtils {
    private Context context;


    private static final String TAG = "SpeechUtils";
    private static SpeechUtils singleton;

    private TextToSpeech textToSpeech; // TTS对象

    public static SpeechUtils getInstance(Context context) {
        if (singleton == null) {
            synchronized (SpeechUtils.class) {
                if (singleton == null) {
                    singleton = new SpeechUtils(context);
                }
            }
        }
        return singleton;
    }

    public SpeechUtils(final Context context) {
        this.context = context;
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int result) {
                if (result == TextToSpeech.SUCCESS) {
//                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setLanguage(Locale.TRADITIONAL_CHINESE);
                    textToSpeech.setPitch(1.0f);// 设置音调，值越大声音越尖（女生），值越小则变成男声,1.0是常规
                    textToSpeech.setSpeechRate(1.0f);
                }else {
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Toast.makeText(context, "数据丢失或不支持", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public void speakText(String text) {
        if (textToSpeech != null) {
            int ret = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            Toast.makeText(context, "ret=" + ret, Toast.LENGTH_SHORT).show();
        }

    }
}
