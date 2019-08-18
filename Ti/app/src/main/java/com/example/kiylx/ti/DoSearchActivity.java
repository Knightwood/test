package com.example.kiylx.ti;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

public class DoSearchActivity extends AppCompatActivity implements Fragment_Search.OnFragmentInteractionListener{
    private static final String TAG = "DoSearchActivity";
    private static final String TEXR_OR_URL="text_or_url";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_search);
        addFragment();
    }
    private void addFragment(){
        Fragment_Search mfragment_search = new Fragment_Search();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fm.beginTransaction();
        fragmentTransaction.add(R.id.dosearch_root,mfragment_search);
        fragmentTransaction.commit();
    }
    @Override
    public void onFragmentInteraction(String s){
        Intent intent = new Intent();
        intent.putExtra(TEXR_OR_URL,s);
        setResult(RESULT_OK,intent);
        finish();

    }
}
/*
* 由Fragment_Search里的文本框监听文本输入，
* 由按键监听来调用DoSearchActivity实现的OnFragmentInteractionListener接口，
* 这个接口把输入的文本放进intent，结束activity。
* 由MainActivity的onActivityResult拿到intent里的文本，
* 由当前显示在屏幕上的webview加载此文本*/