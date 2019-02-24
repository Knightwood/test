package com.example.kiylx.ti;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity  {
    WebList webList_data = new WebList();
    String sharchin="https://www.baidu.com/s?wd=";
    EditText search;
    String text;//搜索框里的内容
    Boolean multflag=true;//multflag决定多标签页的显示和隐藏
    ListView pagelist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ActivityCompat.requestPermissions(this, Manifest.permission.INTERNET,REQUEST_STATUS_CODE);
        // 申请网络权限
        addWebview();
        searchBar();
        //监听搜索框
        back();
        home();
        show();
    }


    public Ti addWebview(){
        //new出一个webview，添加到相应的视图组，并且载入

        LinearLayout view_group = findViewById(R.id.fragment_group);
        Ti web = new Ti(this);

        web.setting(web);
        //给new出来的webview执行设置

        webList_data.push(web);
        //把new出来的webview放进List_web中，以供以后控制webview视图用

		view_group.addView(web);
		//addview(view,index),index控制显示的层级，index一共有0，1，2三层。
		web.setWebViewClient(new CustomWebviewClient());
		web.setWebChromeClient(new CustomWebchromeClient());

        TextView multPage = findViewById(R.id.multpage);
        String cao = String.valueOf(webList_data.getAllnum());
        multPage.setText(cao);
        //给多窗口加上数字

        adapter();

        return web;
    }


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
        webList_data.getTop().loadUrl(string);
        getInfromation(webList_data.getTop());//载入后获取url和标题
    }


    public void multiplePage(View v){
        //已经在layout里设置了click属性，所以这里是点击时调用的函数
        Toast.makeText(MainActivity.this,"fuck",Toast.LENGTH_SHORT).show();
        //得调用viewControl();
        pagelist = findViewById(R.id.pagelist);
        //multflag的真触发显示和假来触发隐藏
        if(multflag){
            //pagelist是显示打开过网页的listview
            //TextView urlview = findViewById(R.id.url);

            webList_data.Top.setVisibility(View.INVISIBLE);//这条是设置现在显示的网页的可见性
            pagelist.setVisibility(View.VISIBLE);
            //urlview.setVisibility(View.INVISIBLE);
            multflag=false;

        }else{
            pagelist.setVisibility(View.GONE);
            webList_data.Top.setVisibility(View.VISIBLE);
            multflag=true;

        }
    }//这里还缺一个在打开多窗口页面时处理返回键的方法，可以用multflag来做点事情

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK&!multflag){
            pagelist = findViewById(R.id.pagelist);
            pagelist.setVisibility(View.GONE);
            webList_data.Top.setVisibility(View.VISIBLE);
            multflag=true;
            return false;
        }
        //这里还要处理其他的返回事件,当返回true，事件就不再向下传递，也就是处理完这个事件就让别的再处理
        return super.onKeyDown(keyCode, event);
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
    }////////////////
    private AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            pagelist = findViewById(R.id.pagelist);
            pagelist.setVisibility(View.GONE);

           Ti web = webList_data.get(position);
           web.setVisibility(View.VISIBLE);

           Toast.makeText(MainActivity.this,"fuck you",Toast.LENGTH_SHORT).show();
        }
    };//设置点击事件

    public void show(){
        //多窗口页面的点击事件，用来显示点击到的网页
        pagelist = findViewById(R.id.pagelist);
        pagelist.setOnItemClickListener(clickListener);

    }///////////////////
    public void adapter(){
        WebAdapter adapter = new WebAdapter(MainActivity.this, webList_data);
        pagelist = findViewById(R.id.pagelist);
        pagelist.setAdapter(adapter);

    }
    public void back(){
        ImageButton imageButton=findViewById(R.id.backButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webList_data.Top.goBack();
            }
        });
    }
    public void home(){
        ImageButton home=findViewById(R.id.homebutton);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webList_data.Top.loadUrl("http://www.baidu.com");
                //addview1();//此行为测试加载特定layout用
            }
        });

    }
    public void addview1(){
        //测试把另外的layout加载进特定位置
        LinearLayout fragment_group=findViewById(R.id.fragment_group);
        LayoutInflater inflater = LayoutInflater.from(this);
        //获取layoutinflater，用它来加载视图，然后用addview把加载进来的视图放进特定位置。
        View view=inflater.inflate(R.layout.pagelist,null,false);
        fragment_group.addView(view,1);
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
