package edu.technopark.arquest.common;


import android.view.MotionEvent;
import android.view.View;

public class CustomViewUtils {
    public static void disableAllTouches(View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }
}
