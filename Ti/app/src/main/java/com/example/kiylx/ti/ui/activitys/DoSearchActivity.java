package com.example.kiylx.ti.ui.activitys;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.kiylx.ti.Tool.ProcessUrl;
import com.example.kiylx.ti.ui.mFragments.Fragment_DoSearch;
import com.example.kiylx.ti.R;

public class DoSearchActivity extends AppCompatActivity implements Fragment_DoSearch.OnFragmentInterfaceListener {
    private static final String TAG = "DoSearchActivity";
    private static final String TEXR_OR_URL = "text_or_url";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_search);
        replaceFragment();
    }

    private void replaceFragment() {
        Fragment_DoSearch mfragment = Fragment_DoSearch.newInstance(getIntent().getStringExtra("current url"));
        getSupportFragmentManager().beginTransaction().replace(R.id.dosearch_root, mfragment).commit();
    }

    /**
     * @param s 字符串
     *          传入字符串，处理好，组装好intent。
     *          当被调用时，setResult并结束这个activity
     */
    @Override
    public void analysisText(String s) {

        Intent intent = new Intent();

        intent.putExtra(TEXR_OR_URL, ProcessUrl.processString(s));
        setResult(RESULT_OK, intent);
        finish();
    }

}
/*
 * 由Fragment_Search里的文本框监听文本输入，
 * 由按键监听来调用DoSearchActivity实现的OnFragmentInteractionListener接口，
 * 这个接口把输入的文本放进intent，结束activity。
 * 由MainActivity的onActivityResult拿到intent里的文本，
 * 由当前显示在屏幕上的webview加载此文本*/

