package com.example.kiylx.ti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements WebAdapter.showView {
    WebList webList = new WebList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ActivityCompat.requestPermissions(this, Manifest.permission.INTERNET,REQUEST_STATUS_CODE);
        // 申请网络权限



        addWebview();
        searchBar();
        //监听搜索框

    }


    public Ti addWebview(){
        //new出一个webview，添加到相应的视图组，并且载入

        LinearLayout view_group = findViewById(R.id.fragment_group);
        Ti web = new Ti(this);

        web.setting(web);
        //给new出来的webview执行设置

        webList.push(web);
        //把new出来的webview放进List_web中，以供以后控制webview视图用

		view_group.addView(web);
		//addview(view,index),index控制显示的层级，index一共有0，1，2三层。
		web.setWebViewClient(new CustomWebviewClient());
		web.setWebChromeClient(new CustomWebchromeClient());

        TextView multPage = findViewById(R.id.multpage);
        String cao = String.valueOf(webList.getAllnum());
        multPage.setText(cao);
        //给多窗口加上数字

        adapter();

        return web;
    }

    String sharchin="https://www.baidu.com/s?wd=";
    EditText search;
    String text;//搜索框里的内容


    public void searchBar(){
         search=findViewById(R.id.edit);
        //Toast.makeText(MainActivity.this,text,Toast.LENGTH_SHORT).show();
        //文字键入完成后

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId==EditorInfo.IME_ACTION_DONE){
                    text = search.getText().toString();
                    if(text.isEmpty()){
                        return false;
                    }else if(text.startsWith("http://")||text.startsWith("https://")){
                        search(text);
                    }else{
                        search(sharchin+text);
                    }

                    Toast.makeText(MainActivity.this,"ok",Toast.LENGTH_SHORT).show();

                }
                return false;

            }
        });//setOnEditorActionListener结束处

    }
    void search(String string){
        webList.getTop().loadUrl(string);
        getInfromation(webList.getTop());//载入后获取url和标题
    }

    public void multiplePage(View v){
        Toast.makeText(MainActivity.this,"fuck",Toast.LENGTH_SHORT).show();
        //得调用viewControl();

        ListView viewk = findViewById(R.id.pagelist);
        //viewk是显示打开过网页的listview
        TextView urlview = findViewById(R.id.url);

        webList.Top.setVisibility(View.INVISIBLE);//这条是设置现在显示的网页的可见性
        viewk.setVisibility(View.VISIBLE);
        urlview.setVisibility(View.INVISIBLE);


    }
    void viewControl(){
        //控制使徒的显示和隐藏，code不同，就要有不同的处理方式
        int code;
    }

    void getInfromation(Ti v){
        v.url=v.getUrl();
        //v.title=v.getTitle();
        TextView url=findViewById(R.id.url);
        //TextView title=findViewById(R.id.title);
        url.setText(v.url);
        //title.setText(v.title);
    }
    public void fuck() {

    }
    public void show(){
        Log.d("回调","work well");
    }
    public void adapter(){
        WebAdapter adapter = new WebAdapter(MainActivity.this,webList);
        ListView listView = findViewById(R.id.pagelist);
        listView.setAdapter(adapter);
    }
    /*
    @Override
    protected void onResume() {
        Ti t = WebList.get(i);

        super.onResume();
        t.onResume();
        t.resumeTimers();}

    @Override
    protected void onPause() {
        super.onPause();
        WebList.get(i).onPause();
        WebList.get(i).pauseTimers();
    }
/*
    @Override
    protected void onDestroy() {
        Ti t = WebList.get(i);
        super.onDestroy();
        if (mRoot != null) {
            mRoot.removeView(WebList.get(i));
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
