package com.example.kiylx.ti.tool.networkpack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.tool.SomeTools;


/**
 * 创建者 kiylx
 * 创建时间 2020/5/24 23:05
 */
public class NetBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "网络广播";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (("android.net.conn.CONNECTIVITY_CHANGE").equals(intent.getAction())) {
            //SomeTools.getXapplication().getStateManager().setNetWorkState(SomeTools.getNetState(context));
            LogUtil.d(TAG, "onReceive:接收到网络状态改变 ");
        }
    }
}
