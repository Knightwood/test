package com.example.kiylx.ti.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.activitys.BookmarkManagerActivity;
import com.example.kiylx.ti.xapplication.Xapplication;
import com.example.kiylx.ti.conf.StateManager;
import com.example.kiylx.ti.downloadpack.DownloadActivity;
import com.example.kiylx.ti.tool.preferences.DefaultPreferenceTool;
import com.example.kiylx.ti.model.ShowPicMode;
import com.example.kiylx.ti.tool.SomeTools;
import com.example.kiylx.ti.ui.activitys.HistorysActivity;
import com.example.kiylx.ti.ui.activitys.BookmarkPageActivity;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.databinding.DialogHomepageSettingBinding;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.interfaces.ControlWebView;
import com.example.kiylx.ti.ui.activitys.Setting2Activity;

/**
 * 主界面的功能界面
 * 准备删除此dialog，把界面迁移到mainactivity。
 */
public class MinSetDialog extends DialogFragment implements View.OnClickListener {
    /*设置，下载，收藏，历史记录，分享，隐身，工具箱，电脑模式*/
    private DialogHomepageSettingBinding homepageSettingBinding;
    private static final String TAG = "MinSetDialog";
    private static WebPage_Info info;
    private ControlWebView controlWebViewInterface;
    private int currentMenu = 0;//第一层就是0，按下工具按钮展现的页面是1
    private StateManager stateManager;


    public static MinSetDialog newInstance(WebPage_Info info) {
        MinSetDialog fragment = new MinSetDialog();
        MinSetDialog.info = info;

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        stateManager = SomeTools.getXapplication().getStateManager();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homepageSettingBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_homepage_setting, null, false);
        homepageSettingBinding.setClicklister(this);
        return homepageSettingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        Boolean pcMode = stateManager.getPcMode();
        homepageSettingBinding.pcMode.setChecked(pcMode);
        Boolean canRecordHistory = stateManager.getDontRecordHistory();
        homepageSettingBinding.hideSelf.setChecked(canRecordHistory);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            Window window = dialog.getWindow();
            WindowManager.LayoutParams lp = window.getAttributes();
            /*if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                //如果是横屏
                lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            } else {
                //竖屏
                lp.gravity = Gravity.BOTTOM;
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;

            }*/
            lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            //lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setLayout(-2, -2);//第一个参数是宽度-1代表默认占满；第二个参数高度-2默认自适应高度（这两个参数也可以直接设置固定宽高）
            window.setBackgroundDrawable(new ColorDrawable(Color.rgb(255, 255, 255)));
            window.setWindowAnimations(R.style.animate_dialog);
            window.setDimAmount(0);//dialog周围全透明
            window.setAttributes(lp);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }
    }

    @Override
    public void onClick(View v) {
        if (controlWebViewInterface != null)
            switch (v.getId()) {
                case R.id.reload_webview:
                    controlWebViewInterface.reload();
                    break;
                case R.id.back1:
                case R.id.toolButton:
                    LogUtil.d(TAG, "onClick: 切换菜单");
                    if (currentMenu == 0) {
                        //当前页是0，要切换到1
                        showView(homepageSettingBinding.toolSecond, homepageSettingBinding.toolFirst);
                        currentMenu = 1;
                    } else {
                        showView(homepageSettingBinding.toolFirst, homepageSettingBinding.toolSecond);
                        currentMenu = 0;
                    }
                    break;
                case R.id.findtext:
                    controlWebViewInterface.searchText();
                    break;
                case R.id.share:
                    controlWebViewInterface.sharing(info.getUrl());
                    break;
                case R.id.pcMode:
                    boolean b = homepageSettingBinding.pcMode.isChecked();
                    if (b)
                        controlWebViewInterface.usePcMode();
                    stateManager.setPcMode(b);
                    break;
                case R.id.hideSelf:
                    boolean b1 = homepageSettingBinding.hideSelf.isChecked();
                    stateManager.setDontRecordHistory(b1);
                    break;
                case R.id.addBookmark:
                    controlWebViewInterface.addtobookmark(info.getUrl());
                    break;
                case R.id.menu:
                    startSetting();
                    break;
                case R.id.button_download:
                    startDownload();
                    break;
                case R.id.button_bookmark:
                    startBookmarked();
                    break;
                case R.id.button_history:
                    startHistory();
                    break;
                case R.id.save_pdf:
                    controlWebViewInterface.printPdf();
                    break;
                case R.id.save_mht:
                    controlWebViewInterface.saveWeb();
                case R.id.show_pic:
                    changePicMode();
                    break;

            }
        if (v.getId() != R.id.toolButton && v.getId() != R.id.back1)//既不是工具箱，也不是点击R.id.back1,就dismiss掉dialogFragment
            dismiss();
        LogUtil.d(TAG, "onClick: " + v.getId());
    }

    /**
     * 更改图片模式，影响网页图片显示
     */
    private void changePicMode() {
        String[] menu = new String[]{"总是显示", "仅WIFI下显示", "禁止显示"};
        int itemId = 0;
        ShowPicMode showPicMode = ShowPicMode.valueOf(DefaultPreferenceTool.getStrings(Xapplication.getInstance(), "showPicatureMode", "ALWAYS"));
        switch (showPicMode) {
            case ALWAYS:
                itemId = 0;
                break;
            case JUSTWIFI:
                itemId = 1;
                break;
            case DONT:
                itemId = 2;
                break;
        }
        new AlertDialog.Builder(getContext()).setSingleChoiceItems(menu, itemId, (dialog, which) -> {
            switch (which) {
                case 0:
                    SomeTools.getXapplication().getStateManager().setShowPicMode(ShowPicMode.ALWAYS);
                    break;
                case 1:
                    SomeTools.getXapplication().getStateManager().setShowPicMode(ShowPicMode.JUSTWIFI);
                    break;
                case 2:
                    SomeTools.getXapplication().getStateManager().setShowPicMode(ShowPicMode.DONT);
                    break;
            }
        }).setTitle("图片模式").create().show();
    }

    private void startDownload() {
        Intent intent = new Intent(getActivity(), DownloadActivity.class);
        startActivity(intent);
    }

    private void startHistory() {

        Intent history_intent = new Intent(getActivity(), HistorysActivity.class);
        startActivity(history_intent);
    }

    /**
     * 启动设置页面
     */
    private void startSetting() {
        //Intent settingIntent = new Intent(getActivity(), SettingActivity.class);
        Intent settingIntent = new Intent(getActivity(), Setting2Activity.class);
        startActivity(settingIntent);
    }

    /**
     * 启动书签页面
     */
    private void startBookmarked() {
        //Intent Bookmark_intent = new Intent(getActivity(), BookmarkPageActivity.class);
        Intent Bookmark_intent = new Intent(getActivity(), BookmarkManagerActivity.class);
        startActivity(Bookmark_intent);
    }

    public void setInterafce(ControlWebView mInterface) {
        this.controlWebViewInterface = mInterface;
    }

    /**
     * @param willShowed 即将被显示的视图
     * @param willHided  即将被隐藏的视图
     */
    private void showView(View willShowed, View willHided) {
        willShowed.setVisibility(View.VISIBLE);
        willHided.setVisibility(View.GONE);
    }

}