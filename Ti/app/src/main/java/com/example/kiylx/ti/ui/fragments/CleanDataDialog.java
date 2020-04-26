package com.example.kiylx.ti.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewDatabase;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.managercore.AboutHistory;
import com.example.kiylx.ti.managercore.CustomAWebView;

import java.io.File;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/26 11:58
 */
public class CleanDataDialog extends DialogFragment implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "清除数据Dialog";

    private boolean cleanFormData;
    private boolean cleanCookies;
    private boolean cleanCache;
    private boolean cleanHistory;
    private boolean cleanLocalStorage;

    public static CleanDataDialog newInstance() {
        return new CleanDataDialog();
    }

    public CleanDataDialog() {
        super();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_cleandata, null);
        initView(v);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
                .setTitle(R.string.cleanData)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        return builder.create();
    }

    private void initView(View v) {
        CheckBox clean_FormData = v.findViewById(R.id.clean_formdata);
        clean_FormData.setOnCheckedChangeListener(this);
        CheckBox clean_Cookies = v.findViewById(R.id.clean_cookies);
        clean_Cookies.setOnCheckedChangeListener(this);
        CheckBox clean_Cache = v.findViewById(R.id.clean_cache);
        clean_Cache.setOnCheckedChangeListener(this);
        CheckBox clean_History = v.findViewById(R.id.clean_history);
        clean_History.setOnCheckedChangeListener(this);
        CheckBox clean_LocalStorage = v.findViewById(R.id.clean_localstorage);
        clean_LocalStorage.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.clean_cookies:
                cleanCookies = isChecked;
                break;
            case R.id.clean_formdata:
                cleanFormData = isChecked;
                break;
            case R.id.clean_cache:
                cleanCache = isChecked;
                break;
            case R.id.clean_history:
                cleanHistory = isChecked;
                break;
            case R.id.clean_localstorage:
                cleanLocalStorage = isChecked;
                break;

        }
        Log.d(TAG, "onClick: " + buttonView.getId() + "  网页存储："
                + cleanLocalStorage + "  历史记录："
                + cleanHistory + "  缓存："
                + cleanCache + "  表单："
                + cleanFormData + "  cookies："
                + cleanCookies);

    }

    private void delete() {
        if (cleanCookies) {
            CookieManager manager = CookieManager.getInstance();
            manager.removeAllCookies(null);
        }
        if (cleanFormData) {
            WebViewDatabase database = WebViewDatabase.getInstance(getActivity());
            database.clearFormData();
        }
        if (cleanCache) {
            new CustomAWebView(getContext()).clearCache(true);
        }
        if (cleanHistory) {
            AboutHistory aboutHistory = AboutHistory.get(getActivity());
            aboutHistory.deleteAll();
        }
        if (cleanLocalStorage) {
            WebStorage.getInstance().deleteAllData();
        }
    }
    /**
     * 清除缓存
     *
     * @param context 上下文
     */
    public static void clearCache(Context context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // 清除cookie
                CookieManager.getInstance().removeAllCookies(null);
            } else {
                CookieSyncManager.createInstance(context);
                CookieManager.getInstance().removeAllCookie();
                CookieSyncManager.getInstance().sync();
            }

            new WebView(context).clearCache(true);

            File cacheFile = new File(context.getCacheDir().getParent() + "/app_webview");
            clearCacheFolder(cacheFile, System.currentTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static int clearCacheFolder(File dir, long time) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, time);
                    }
                    if (child.lastModified() < time) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

}
