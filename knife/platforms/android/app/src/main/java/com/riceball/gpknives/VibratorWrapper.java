package com.riceball.gpknives;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;


public class VibratorWrapper {

    private static Vibrator s_vibrator = null;
    private static VibratorWrapper mInstace = null;

    public static VibratorWrapper getInstance() {
        if (null == mInstace) {
            mInstace = new VibratorWrapper();
        }
        return mInstace;
    }
 
    public void init(Context context) {
        s_vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
//        Log.v("jswrapper", "vibrate init, s_vibrator:" + (s_vibrator != null));
    }

    public static void vibrateLong() {
        vibrate(400);
    }

    public static void vibrateShort() {
        vibrate(30);
    }

    public static void vibrate(Integer time) {
        if (s_vibrator != null ) s_vibrator.vibrate(time);
//        Log.v("jswrapper", "vibrate time:" + time + " s_vibrator:" + (s_vibrator != null));
    }
}
