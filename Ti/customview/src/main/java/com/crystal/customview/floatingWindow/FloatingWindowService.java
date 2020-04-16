package com.crystal.customview.floatingWindow;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

public abstract class FloatingWindowService extends Service {
    private static final String TAG = "floatTest父类";

    private WindowManager manager;
    private WeakReference<Context> mContext;
    private View mRoot;//根布局
    private WindowManager.LayoutParams lp;//默认的参数

    @Override
    public void onCreate() {
        mContext = new WeakReference<>(getApplicationContext());
        manager = (WindowManager) mContext.get().getSystemService(Context.WINDOW_SERVICE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: 被调用了");
        showFloatingWindow();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    private void showFloatingWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                return;
            }
        }
        mRoot = setLayout();
        mRoot.setOnTouchListener(new FloatingOnTouchListener());

        setLayoutParams();
        addView(mRoot, lp);
    }


    /**
     * 被子类重写，把要展示的view作为返回参数
     *
     * @return
     */
    public abstract View setLayout();

    /**
     * 被子类重写，给予此抽象类lp
     *
     * @return
     */
    public abstract WindowManager.LayoutParams setLp();

    /**
     * 返回默认的lp,以便修改默认的lp属性
     *
     * @return
     */
    public WindowManager.LayoutParams getLp() {
        return this.lp;
    }

    /**
     * 初始化默认的lp
     *
     * @return
     */
    private void setLayoutParams() {
        if (lp == null) {
            this.lp = setLp();
        }
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
        //FLAG_NOT_TOUCH_MODAL，可以让此view被正常点击和触摸，同时也可以让此view之外的位置触摸
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        /*WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        * 窗口标志：此窗口永远不会获得按键输入焦点，因此用户无法向其发送按键或其他按钮事件。
        * 这些将转至其背后的任何可聚焦窗口。 无论是否明确设置此标志，都将启用FLAG_NOT_TOUCH_MODAL。
        *设置此标志还意味着该窗口将不需要与软输入法进行交互，
        * 因此它将对Z进行排序和定位，而与任何活动的输入方法无关（通常这意味着它在输入法之上被Z排序， 因此它可以使用全屏显示内容，并在需要时覆盖输入法。
        * */
    }

    /**
     * 把view添加进windowManager
     *
     * @param v  视图
     * @param lp 视图的参数
     * @return 是否添加成功
     */
    public boolean addView(View v, WindowManager.LayoutParams lp) {
        Log.d(TAG, "addView: 添加了视图");
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
    public boolean removeView(View v) {
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
    public boolean updateView(View view, WindowManager.LayoutParams params) {
        try {
            manager.updateViewLayout(view, params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();

                    int movedX = nowX - x;
                    int movedY = nowY - y;

                    x = nowX;
                    y = nowY;

                    lp.x += movedX;
                    lp.y += movedY;

                    updateView(mRoot, lp);
                    break;
                default:
                    break;

            }

            return false;
        }
    }
}
