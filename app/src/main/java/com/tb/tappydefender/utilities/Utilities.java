package com.tb.tappydefender.utilities;

import android.os.Debug;

public class Utilities {
    public static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

    public static String FASTEST_SCORE_ID = "fastestTime";
    public static String HIGH_SCORES_FILE_ID = "HiScores";

    public static boolean isDebuggerAttached(){
        return Debug.isDebuggerConnected();
    }

    public static String formatTime(long time){
        long seconds = time / 1000;
        long thounsandths = time - (seconds * 1000);
        String strThoundsandths = "" + thounsandths;
        if(thounsandths < 100){strThoundsandths = "0" + thounsandths;}
        if(thounsandths < 10){strThoundsandths = "0" + strThoundsandths;}
        String  stringTime =  seconds + "." + strThoundsandths;
        return stringTime;
    }
}