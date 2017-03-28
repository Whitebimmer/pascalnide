package com.duy.pascal.frontend.view.screen.console;

import android.util.Log;

/**
 * Created by Duy on 26-Mar-17.
 */

public class ConsoleDebug {
    public final static String TAG = ConsoleDebug.class.getSimpleName();
    private static boolean DEBUG = false;

    public static void log(char c) {
        if (DEBUG) Log.d(TAG, "emit char: " + Character.toString(c));
    }

    public static void log(String s) {
        if (DEBUG) Log.d(TAG, "emit string: " + s);
    }

    public static String bytesToString(byte[] data, int base, int length) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < length; i++) {
            byte b = data[base + i];
            if (b < 32 || b > 126) {
                buf.append(String.format("\\x%02x", b));
            } else {
                buf.append((char) b);
            }
        }
        return buf.toString();
    }
}