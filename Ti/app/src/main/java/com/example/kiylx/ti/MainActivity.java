package com.example.kiylx.ti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    List_web group = new List_web();
    //int i=0;//标记网页数

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ActivityCompat.requestPermissions(this, Manifest.permission.INTERNET,REQUEST_STATUS_CODE);

        search();
        //执行搜索

    }


    public Ti addWebview(String string){
        //new出一个webview，添加到相应的视图组，并且载入
        LinearLayout view_group = findViewById(R.id.fragment_group);
        Ti web = new Ti(this);
        web.setting(web);
        //给new出来的webview执行设置
         //group.add(i,web);
        //把new出来的webview放进List_web中，以供以后控制webview视图用

		view_group.addView(web);
		web.setWebViewClient(new CustomWebviewClient());
		web.setWebChromeClient(new CustomWebchromeClient());
		web.loadUrl(string);
        return web;
    }

    int flsgs;
    String sharchin="https://www.baidu.com/s?wd=";
    EditText search;
    String text;//搜索框里的内容
    int page_flags;

    public void search(){
        page_flags=0;//标记在同一个搜索框里搜索，这里为0，当第一次用这个搜索框搜索，标记为1。
         search=findViewById(R.id.edit);
         //search.set
         search.addTextChangedListener(new TextWatcher() {
            //搜索框输入完成，后判断字符串，当按下回车键，开始搜索
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //表示这是网址，还是普通字符串
                text = search.getText().toString();
                //Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();
                //文字键入完成后
                if(text.isEmpty()){
                    return;
                }else if(text.startsWith("http://")||text.startsWith("https://")){
                    flsgs=0;
                }else{
                    flsgs=1;
                }
                search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if(actionId==EditorInfo.IME_ACTION_DONE){
                            page_flags=1;
                            if(flsgs==0){
                                //+调用一个函数，判断是网址还是普通文字
                                addWebview(text);
                            }else {
                                addWebview(sharchin+text);
                            }
                            Toast.makeText(MainActivity.this,"ok",Toast.LENGTH_SHORT).show();

                        }
                        return false;
                    }
                });//setOnEditorActionListener结束处

            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }/*
    @Override
    protected void onResume() {
        Ti t = group.get(i);

        super.onResume();
        t.onResume();
        t.resumeTimers();}

    @Override
    protected void onPause() {
        super.onPause();
        group.get(i).onPause();
        group.get(i).pauseTimers();
    }
/*
    @Override
    protected void onDestroy() {
        Ti t = group.get(i);
        super.onDestroy();
        if (mRoot != null) {
            mRoot.removeView(group.get(i));
        }
        if (t != null) {
            t.stopLoading();
            t.clearMatches();
            t.clearHistory();
            t.clearSslPreferences();
            t.clearCache(true);
            t.loadUrl("about:blank");
            t.removeAllViews();
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                t.removeJavascriptInterface("AndroidNative");
            }
            t.destroy();
        }
        t = null;
    }

*/

}
