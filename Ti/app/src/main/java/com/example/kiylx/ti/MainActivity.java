package com.example.kiylx.ti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    List_web group = new List_web();
    int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ActivityCompat.requestPermissions(this, Manifest.permission.INTERNET,REQUEST_STATUS_CODE);

        search();
        //执行搜索

    }
	

    public WebView addWebview(){
        //new出一个webview，添加到相应的试图组，并且载入网址
		LinearLayout temp = findViewById(R.id.fragment_group);
        Ti web = new Ti(this);
        web.setting(web);
        //给new出来的webview执行设置
        group.add(i,web);
        //把new出来的webview放进List_web中，以供以后控制webview视图用
        i=i+1;
		temp.addView(web);
		web.loadUrl("http://www.baidu.com");
        return web;
    }

    public void find(){


    }

    public void search(){
        //监听搜索框，当键入回车键时开始搜索；
    EditText search =findViewById(R.id.edit);
    search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if(actionId == EditorInfo.IME_ACTION_DONE){
               addWebview();
                Toast.makeText(MainActivity.this,"ok",Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    }
    public void initweb(WebView view){
        //初始化设置好webview
    }


}
