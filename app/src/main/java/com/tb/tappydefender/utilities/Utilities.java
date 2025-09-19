package com.tb.tappydefender.utilities;

import android.os.Debug;

public class Utilities {
    public static final long DOUBLE_CLICK_TIME_DELTA = 300;//milliseconds

    public static String FASTEST_SCORE_ID = "fastestTime";
    public static String HIGH_SCORES_FILE_ID = "HiScores";

    public static boolean isDebuggerAttached(){
        return Debug.isDebuggerConnected();
    }
}