package com.example.kiylx.ti.ui.setting2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.tool.preferences.DefaultPreferenceTool;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/27 10:47
 */
public class WebTextSizeFragment extends Fragment {
    private View v;
    private WebView webView;
    private SeekBar seekBar;
    private Button resetTextSize;


    public WebTextSizeFragment newInstance() {
        return new WebTextSizeFragment();
    }

    public WebTextSizeFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.webtextsize_view, container, false);
        initView();
        return v;
    }

    private void initView() {
        int defTextSize= DefaultPreferenceTool.getInt(getContext(),"text_zoom",100);

        resetTextSize = v.findViewById(R.id.resetSize);

        webView = (WebView) v.findViewById(R.id.textsize_webview);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setTextZoom(defTextSize);//初始化显示的文字大小
        webView.loadUrl("file:///android_asset/TextSizeHtml.html");

        seekBar = v.findViewById(R.id.textSizeSeekBar);
        seekBar.setProgress(defTextSize-50);//初始化滑动条
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                reloadWeb(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        resetTextSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setProgress(50);
                reloadWeb(50);

            }
        });

    }

    /**
     * @param textSize 字体大小缩放值，seekbar取值范围是0-150，对应的webview的字体缩放是50-200
     *                 此方法缩放预览webview，并把缩放值写入sharedPreference
     */
    private void reloadWeb(int textSize) {
        webView.getSettings().setTextZoom(textSize + 50);
        webView.reload();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        preferences.edit().putInt("text_zoom",textSize+50).apply();
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
