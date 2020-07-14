package com.example.kiylx.ti.conf;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;


import com.example.kiylx.ti.tool.preferences.DefaultPreferenceTool;
import com.example.kiylx.ti.tool.networkpack.NetState;
import com.example.kiylx.ti.model.ShowPicMode;
import com.example.kiylx.ti.tool.SomeTools;
import com.example.kiylx.ti.tool.networkpack.NetworkMana;

import java.lang.ref.WeakReference;
import java.util.Observable;

/**
 * 创建者 kiylx
 * 创建时间 2020/7/4 23:41
 * 管理一些状态信息，使用观察者推送
 */
public class StateManager extends Observable {
    private static final String TAG = "stateManager";
    private WeakReference<Context> mContext;

    private ShowPicMode showPicMode;//是否显示网页图片，1：禁止显示图片；2：始终显示图片；3：在wifi下显示图片
    private boolean isPrivacy;//是否使用了隐私模式
    private boolean DNT;//是否发送不要跟踪的请求
    private NetworkMana netWorkState;//网络状态集合
    private NetState netState;//当前网络状态


    public StateManager(Context context) {
        mContext = new WeakReference<>(context);
        initData();
        initPreference();
    }

    /**
     * 初始化全局数据
     */
    private void initData() {
        //netWorkState //初始化网络情况
        //获取当前网络状态
        netState=SomeTools.getCurrentNetwork(mContext.get());
    }

    /**
     * 初始化全局preference数据
     */
    private void initPreference() {
        DNT = DefaultPreferenceTool.getBoolean(mContext.get(), "dont_track", false);
        showPicMode = ShowPicMode.valueOf(DefaultPreferenceTool.getStrings(mContext.get(), "showPicatureMode", "ALWAYS"));
        isPrivacy = DefaultPreferenceTool.getBoolean(mContext.get(), "privacyMode", false);

    }


    public ShowPicMode getShowPicMode() {
        return showPicMode;
    }

    /**
     * @return 若是禁止显示图片返回false，若是数据网络下禁止显示图片，且现在就是出于数据网络下，返回false。
     * 其余情况都是返回true，也就是可以显示图片，即使没有网络
     */
    public boolean canShowPic() {
        if (showPicMode == ShowPicMode.DONT) {//禁止显示图片或是没有网络连接
            return false;
        }

        if (showPicMode == ShowPicMode.JUSTWIFI && netState==NetState.DATA) {
            return false;
        }
        return true;
    }

    public void setShowPicMode(ShowPicMode showPicMode) {
        this.showPicMode = showPicMode;
        PreferenceManager.getDefaultSharedPreferences(mContext.get()).edit().putString("showPicatureMode", showPicMode.toString()).apply();
    }

    public boolean isPrivacy() {
        return isPrivacy;
    }

    public void setPrivacy(boolean privacy) {
        isPrivacy = privacy;
        PreferenceManager.getDefaultSharedPreferences(mContext.get()).edit().putBoolean("privacyMode", privacy).apply();
    }


    public boolean isDNT() {
        return DNT;
    }

    public void setDNT(boolean DNT) {
        this.DNT = DNT;
    }

    public NetworkMana getNetWorkState() {
        return netWorkState;
    }

    public void setNetWorkState(NetworkMana netWorkState) {
        this.netWorkState = netWorkState;
        Log.d(TAG, "setNetWorkState: data " + netWorkState.get(NetState.DATA) + "  wifi: " + netWorkState.get(NetState.WIFI));
    }

    /**
     * 推送给订阅者更新消息
     */
    private void pushData() {
        setChanged();
        notifyObservers();
    }

    public NetState getNetState() {
        return netState;
    }

    public void setNetState(NetState netState) {
        this.netState = netState;
    }
}
