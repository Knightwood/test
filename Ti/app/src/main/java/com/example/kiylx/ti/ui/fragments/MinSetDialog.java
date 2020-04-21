package com.example.kiylx.ti.ui.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
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

import com.example.kiylx.ti.downloadPack.DownloadActivity;
import com.example.kiylx.ti.ui.activitys.HistoryActivity;
import com.example.kiylx.ti.ui.activitys.SettingActivity;
import com.example.kiylx.ti.ui.activitys.BookmarkPageActivity;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.databinding.DialogHomepageSettingBinding;
import com.example.kiylx.ti.corebase.WebPage_Info;
import com.example.kiylx.ti.myInterface.ControlWebView;

/**
 * 主界面的功能界面
 */
public class MinSetDialog extends DialogFragment implements View.OnClickListener {
    /*设置，下载，收藏，历史记录，分享，隐身，工具箱，电脑模式*/
    private DialogHomepageSettingBinding homepageSettingBinding;
    private static final String TAG = "MinSetDialog";
    private static WebPage_Info info;
    private ControlWebView controlWebViewInterface;


    public static MinSetDialog newInstance(WebPage_Info info) {
        MinSetDialog fragment = new MinSetDialog();
        MinSetDialog.info = info;

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        homepageSettingBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_homepage_setting, null, false);
        //mRecyclerView= homepageSettingBinding.optionsRecyclerview;
        //mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //updateUI(optionslist);
        homepageSettingBinding.setClicklister(this);
        return homepageSettingBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setBackgroundDrawable(new ColorDrawable(Color.rgb(255, 255, 255)));
            window.setWindowAnimations(R.style.animate_dialog);
            window.setAttributes(lp);
            //设置点击外部可以取消对话框
            setCancelable(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reload_webview:
                controlWebViewInterface.reload();
                break;
            case R.id.findtext:
                controlWebViewInterface.searchText();
                break;
            case R.id.share:
                controlWebViewInterface.sharing(info.getUrl());
                break;
            case R.id.pcMode:
                controlWebViewInterface.usePcMode();
                break;
            case R.id.hideSelf:
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

        }
        dismiss();
        Log.d(TAG, "onClick: " + v.getId());
    }

    private void startDownload() {
        Intent intent = new Intent(getActivity(), DownloadActivity.class);
        startActivity(intent);
    }

    private void startHistory() {
        Intent history_intent = new Intent(getActivity(), HistoryActivity.class);
        startActivity(history_intent);
    }

    /**
     * 启动设置页面
     */
    private void startSetting() {
        Intent settingIntent=new Intent(getActivity(), SettingActivity.class);
        startActivity(settingIntent);
    }

    /**
     * 启动书签页面
     */
    private void startBookmarked() {
        Intent Bookmark_intent = new Intent(getActivity(), BookmarkPageActivity.class);
        startActivity(Bookmark_intent);
    }

    public void setInterafce(ControlWebView mInterface) {
        this.controlWebViewInterface = mInterface;
    }

}