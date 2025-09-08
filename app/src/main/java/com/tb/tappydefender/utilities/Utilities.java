package com.tb.tappydefender.utilities;

import android.os.Debug;

public class Utilities {
    public static boolean isDebuggerAttached(){
        return Debug.isDebuggerConnected();
    }
}