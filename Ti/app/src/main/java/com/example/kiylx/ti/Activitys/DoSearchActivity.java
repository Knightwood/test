package com.example.kiylx.ti.Activitys;

import android.content.Intent;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.example.kiylx.ti.model.ProcessRecordItem;
import com.example.kiylx.ti.Fragments.Fragment_DoSearch;
import com.example.kiylx.ti.R;

public class DoSearchActivity extends AppCompatActivity implements Fragment_DoSearch.OnFragmentInteractionListener {
    private static final String TAG = "DoSearchActivity";
    private static final String TEXR_OR_URL="text_or_url";
    private String searchEngine = " https://mijisou.com/search?q=";
    //private static final String CURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_search);
        addFragment();
    }
    private void addFragment(){
        //Fragment_DoSearch mfragment_Do_search = new Fragment_DoSearch();
        Fragment_DoSearch mfragment_Do_search =Fragment_DoSearch.newInstance(getIntent().getStringExtra("current url"));
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction= fm.beginTransaction();
        fragmentTransaction.add(R.id.dosearch_root, mfragment_Do_search);
        fragmentTransaction.commit();
    }
    @Override
    public void onFragmentInteraction(String s){
        ProcessRecordItem tmp=new ProcessRecordItem();
        Intent intent = new Intent();
        if(tmp.processString(s)){
        intent.putExtra(TEXR_OR_URL,s);}else{
            s=searchEngine+s;
            intent.putExtra(TEXR_OR_URL,s);
        }
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

