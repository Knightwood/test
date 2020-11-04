package com.crystal.fucktoollibrary.tools;

import android.util.Log;
import android.widget.Toast;


import org.jetbrains.annotations.NotNull;

/**
 * 创建者 kiylx
 * 创建时间 2020/7/29 23:00
 */
public class LogUtil {

    public static final int VERBOSE = 1;
    public static final int DEBUG = 2;
    public static final int INFO = 3;
    public static final int WARN = 4;
    public static final int ERROR = 5;
    public static final int NOTHING = 6;
    public static final int nowLevel = VERBOSE;
    /*
     *在方法中，相应的日志级别大于等于nowLevel值，才可打印。
     * 比如，nowLevel是6，则，所有的日志级别都小于6，所有的日志都不可打印。
     */

    public static void v(String tag, String msg, Object... args) {
        if (nowLevel <= VERBOSE) {
            if (args == null||args.length==0)
                Log.v(tag, msg);
            else
                Log.v(tag, String.format(msg, args));
        }
    }

    public static void d(String tag, String msg, Object... args) {
        if (nowLevel <= DEBUG) {
            if (args == null||args.length==0)
                Log.d(tag, msg);
            else
                Log.d(tag, String.format(msg, args));

        }
    }

    public static void i(String tag, String msg, Object... args) {
        if (nowLevel <= INFO) {
            if (args == null||args.length==0)
                Log.i(tag, msg);
            else
                Log.i(tag, String.format(msg, args));

        }
    }

    public static void w(String tag, String msg, Object... args) {
        if (nowLevel <= WARN) {
            if (args == null||args.length==0)
                Log.w(tag, msg);
            else
                Log.w(tag, String.format(msg, args));

        }
    }

    public static void e(String tag, String msg, Object... args) {
        if (nowLevel <= ERROR) {
            if (args == null||args.length==0)
                Log.w(tag, msg);
            else
                Log.w(tag, String.format(msg, args));

        }
    }
    public static void showToast(String message) {
            Toast.makeText(Xapplication.getInstance(), message, Toast.LENGTH_LONG).show();
    }

}
