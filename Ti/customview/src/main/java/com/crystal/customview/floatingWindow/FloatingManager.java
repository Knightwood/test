package com.crystal.customview.floatingWindow;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/16 11:26
 */
public class FloatingManager {
    private WindowManager manager;
    private static FloatingManager mInstance;
    private WeakReference<Context> mContext;

    public static FloatingManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new FloatingManager(context);
        }
        return mInstance;
    }

    private FloatingManager(Context context) {
        mContext = new WeakReference<>(context);
        manager = (WindowManager) mContext.get().getSystemService(Context.WINDOW_SERVICE);
    }

    /**
     * 把view添加进windowManager
     *
     * @param v  视图
     * @param lp 视图的参数
     * @return 是否添加成功
     */
    protected boolean addView(View v, WindowManager.LayoutParams lp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
/*在Android 8.0之前，悬浮窗口设置可以为TYPE_PHONE，这种类型是用于提供用户交互操作的非应用窗口。
　　而Android 8.0对系统和API行为做了修改，包括使用SYSTEM_ALERT_WINDOW权限的应用无法再使用一下窗口类型来在其他应用和窗口上方显示提醒窗口：
- TYPE_PHONE
- TYPE_PRIORITY_PHONE
- TYPE_SYSTEM_ALERT
- TYPE_SYSTEM_OVERLAY
- TYPE_SYSTEM_ERROR
　　如果需要实现在其他应用和窗口上方显示提醒窗口，那么必须该为TYPE_APPLICATION_OVERLAY的新类型。 */
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            lp.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

        try {
            manager.addView(v, lp);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除view
     *
     * @param v
     * @return
     */
    protected boolean removeView(View v) {
        try {
            manager.removeView(v);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 更新悬浮窗参数
     *
     * @param view
     * @param params
     * @return
     */
    protected boolean updateView(View view, WindowManager.LayoutParams params) {
        try {
            manager.updateViewLayout(view, params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
