package com.example.kiylx.ti.ui.setting2;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kiylx.ti.R;

public class AboutFragment extends Fragment {
WebView webView;
    public static AboutFragment newInstance(){
        return new AboutFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_about,container,false);
        webView=v.findViewById(R.id.webview_aboutapp);
        initWeb();
        return v;
    }

    private void initWeb() {
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setTextZoom(100);//初始化显示的文字大小
        webView.loadUrl("file:///android_asset/aboutApp.html");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        webView.loadUrl("about:blank");
        webView.stopLoading();
        webView.onPause();
        webView.clearHistory();
        webView.setWebViewClient(null);
        webView.removeAllViews();
        webView.destroy();
    }

}
