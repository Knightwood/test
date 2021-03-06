package com.example.kiylx.ti.conf;

import android.content.Context;
import android.os.Environment;
import android.preference.PreferenceManager;


import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.activitys.ContentToUrlActivity;
import com.example.kiylx.ti.xapplication.Xapplication;
import com.example.kiylx.ti.tool.preferences.DefaultPreferenceTool;
import com.example.kiylx.ti.tool.networkpack.NetState;
import com.example.kiylx.ti.model.ShowPicMode;
import com.example.kiylx.ti.tool.SomeTools;
import com.example.kiylx.ti.tool.networkpack.NetworkMana;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * 创建者 kiylx
 * 创建时间 2020/7/4 23:41
 * 管理一些状态信息，使用观察者推送
 */
public class StateManager extends Observable {
    private volatile static StateManager stateManager;
    private static final String TAG = "stateManager";
    private WeakReference<Context> mContext;

    private ShowPicMode showPicMode;//是否显示网页图片，1：禁止显示图片；2：始终显示图片；3：在wifi下显示图片
    private boolean isPrivacy;//是否使用了隐私模式
    private boolean DNT;//是否发送不要跟踪的请求
    private NetworkMana netWorkState;//网络状态集合
    private NetState netState;//当前网络状态
    private Boolean pcMode;//是否请求桌面版网页
    private Boolean dontRecordHistory;//true为不记录历史，false为可以记录历史记录
    private List<String> jsFilePathList;//存储js文件的路径


    public static StateManager getInstance(Context context){
        if (stateManager==null){
            synchronized (StateManager.class){
                if (stateManager==null){
                    stateManager=new StateManager(context);
                }
            };
        }
        return stateManager;
    }

    private StateManager(Context context) {
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
        initJsList();

    }

    private void initJsList() {
        if (jsFilePathList==null)
            jsFilePathList=new ArrayList<>();
        File jsFolder= mContext.get().getFilesDir();

    }

    /**
     * 初始化全局preference数据
     */
    private void initPreference() {
        DNT = DefaultPreferenceTool.getBoolean(mContext.get(), "dont_track", false);
        showPicMode = ShowPicMode.valueOf(DefaultPreferenceTool.getStrings(mContext.get(), "showPicatureMode", "ALWAYS"));
        isPrivacy = DefaultPreferenceTool.getBoolean(mContext.get(), "privacyMode", false);
        pcMode=DefaultPreferenceTool.getBoolean(Xapplication.getInstance(), "pc_mode", false);
        dontRecordHistory =DefaultPreferenceTool.getBoolean(Xapplication.getInstance(), "record_history", false);
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
        LogUtil.d(TAG, "setNetWorkState: data " + netWorkState.get(NetState.DATA) + "  wifi: " + netWorkState.get(NetState.WIFI));
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

    public Boolean getPcMode() {
        return pcMode;
    }

    public void setPcMode(Boolean pcMode) {
        this.pcMode = pcMode;
        PreferenceManager.getDefaultSharedPreferences(Xapplication.getInstance()).edit().putBoolean("pc_mode", pcMode).apply();
    }

    public Boolean getDontRecordHistory() {
        return dontRecordHistory;
    }

    public void setDontRecordHistory(Boolean dontRecordHistory) {
        this.dontRecordHistory = dontRecordHistory;
        PreferenceManager.getDefaultSharedPreferences(Xapplication.getInstance()).edit().putBoolean("record_history", pcMode).apply();
    }

    public List<String> getJsFilePathList() {
        return jsFilePathList;
    }
    public void addJsPathToList(String path){
        this.jsFilePathList.add(path);
    }
}
