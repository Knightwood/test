package com.example.kiylx.ti;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.example.kiylx.ti.model.CuViewModel;

public class MainActivity extends AppCompatActivity implements Fragment_web.create, MultPage_DialogFragment.NewPagebutton_click, MultPage_DialogFragment.DeletePage,MultPage_DialogFragment.SwitchPage {
    private static final String TAG="MainActivity";

    /*WebList webList_data = new WebList();
    String sharchin="https://www.baidu.com/s?wd=";
    EditText search;
    String text;//搜索框里的内容
    Boolean multflag=true;//multflag决定多标签页的显示和隐藏
    ListView pagelist;
    private CuViewModel viewmodel;*/

    Clist mClist= new Clist();
    FrameLayout f1;
    CurrentUse_WebPage_Lists sCurrentUse_webPage_lists;
    int currect=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        f1=findViewById(R.id.Webview_group);

        //LayoutInflater inflater =getLayoutInflater();
        //Layout vi=inflater.inflate(R.layout.mult_root);

        //ActivityMainBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        //UserBean userBean = new UserBean("whye",7);
        //binding.setUser(userBean);


        /*viewmodel = ViewModelProviders.of(this).get(CuViewModel.class);
        viewmodel.getWebViewlist().observe(this, new Observer<ArrayList<View>>() {
            @Override
            public void onChanged(@Nullable ArrayList<View> list) {
                for (View item : list) {
                    list.add(new WebView(MainActivity.this));
                }
            }
        });*/
        addWebviewtohome();
        toolbaract();

    }

    private void addWebviewtohome() {
        //作用是把数组第一个webview对象展示在面前
        newWebView(0);
        f1.addView(mClist.getTop(0));
    }
    @Override
    public void click_newPagebutton(){
        //新建标签页
        newTab();
    }
    @Override
    public int delete_page(int position){
        if(1==mClist.size()){
            //如果删除这个webview后没有其他的webview了，那就新建标签页
            mClist.getTop(0).loadUrl(null);
            return 0;
        }
        if(position>currect){
            mClist.destroy(position);
            delete_CUWL(position);
            return 0;
        }else if(position<currect){
            f1.removeView(mClist.getTop(currect));
            mClist.destroy(position);
            delete_CUWL(position);
            currect--;
        }else{
            if(position!=mClist.size()-1){
                //currect==position时，只要不是删除最后一个，就都这样操作：移除当前webview，删除webivew，把新提升上来的当前位置的webview添加进视图
                f1.removeView(mClist.getTop(position));
                mClist.destroy(position);
                delete_CUWL(position);
                f1.addView(mClist.getTop(position));
            }else{
                f1.removeView(mClist.getTop(position));
                currect--;
                mClist.destroy(position);
                delete_CUWL(position);
                f1.addView(mClist.getTop(currect));
            }
        }
        return 0;
    }
    @Override
    public void switchPage(int pos){
        f1.removeView(mClist.getTop(currect));
        f1.addView(mClist.getTop(pos));
        currect=pos;
    }

    private void delete_CUWL(int i){
        //从Clist里删除了webview，sCurrentUse_webPage_lists也要保持一致
        sCurrentUse_webPage_lists= CurrentUse_WebPage_Lists.get();
        sCurrentUse_webPage_lists.delete(i);
    }

    @Override
    //Fragment_web调用
    //废弃
    public WebView addWebview(){
        WebView web = new WebView(this);
        set1(web);
        //给new出来的webview执行设置
        web.setWebViewClient(new CustomWebviewClient());
        web.setWebChromeClient(new CustomWebchromeClient());
        return web;
    }
    /*废弃
    public void addhome(){
        FrameLayout f1=findViewById(R.id.Webview_group);
        LayoutInflater inflater=getLayoutInflater();
        View view = inflater.inflate(R.layout.home,null);
        //f1.addView(view);
        newWebView();//准备好webview
    }*/

    private void newWebView(int i) {
        //新建webview并放进数组
        WebView web = new WebView(this);
        set1(web);
        //给new出来的webview执行设置
        web.setWebViewClient(new CustomWebviewClient());
        web.setWebChromeClient(new CustomWebchromeClient());
        mClist.addToFirst(web,i);
        //addToFirst(web,i)其实没有做限制，int i指示放在哪，默认是0，既是第一个位置。
        sCurrentUse_webPage_lists= CurrentUse_WebPage_Lists.get();
        sCurrentUse_webPage_lists.add(web.getTitle(),web.getUrl(),0);
        //把网页信息保存进去，flags记为1，表示是一个newTab，不计入历史记录
    }
    public void newTab(){
        //由多窗口的新建主页按钮调用，作用是新建webview放进mclist的第0号位置，remove掉旧的webivew视图，刷新视图。
        f1.removeView(mClist.getTop(0));
        addWebviewtohome();
        currect=0;
    }
    //把有webview的fragment放进webview_group这个视图里
    //废弃
    public void addwebpage(){
        FragmentManager fragmentManager= getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        Fragment_web fragment_web = new Fragment_web();
        fragmentTransaction.add(R.id.Webview_group, fragment_web);
        fragmentTransaction.commit();
        //fragmentManager.findFragmentByTag();


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Check if the key event was the Back button and if there's history
        //这里还要处理其他的返回事件,当返回true，事件就不再向下传递，也就是处理完这个事件就让别的再处理
        if((keyCode==KeyEvent.KEYCODE_BACK)&&mClist.getTop(0).canGoBack()){
            mClist.getTop(0).goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

//工具栏设置
    private void toolbaract(){
        Toolbar bar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(bar);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //禁止显示标题
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClist.getTop(currect).goBack();
                Toast.makeText(MainActivity.this,"dmji",Toast.LENGTH_SHORT).show();

            }
        });


        //设置移除图片  如果不设置会默认使用系统灰色的图标
        bar.setOverflowIcon(getResources().getDrawable(R.drawable.icon_action));
//填充menu
        bar.inflateMenu(R.menu.toolbar_menu);
//设置点击事件
        bar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.action_mult:
                        Log.i(TAG, "onClick: 多窗口按钮被触发");
                        mult_dialog();
                        break;
                    case R.id.action_star:
                        Log.i(TAG, "onClick: 收藏按钮被触发");

                        break;
                    case R.id.action_flash:

                        Log.i(TAG, "onClick: 刷新按钮被触发");
                        mClist.getTop(currect).reload();
                        break;
                    case R.id.action_menu:

                        Log.i(TAG, "onClick: 菜单按钮被触发");

                    default:
                        break;
                }
                return false;
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }


//搜索框代码
    public void searchBar(View v){
         search_dialog();
    }
    private void search_dialog(){
        //展示搜索框
        FragmentManager fm = getSupportFragmentManager();
        BottomDialogFragment bottomDialogFragment = new BottomDialogFragment();
        bottomDialogFragment.show(fm,"fragment_bottom_dialog");
    }

    public void mult_bottom(View v){
        mult_dialog();
    }
    private void mult_dialog(){
        //展示多窗口
        FragmentManager fm = getSupportFragmentManager();
        MultPage_DialogFragment md=new MultPage_DialogFragment();
        md.show(fm,"fragment_multPage_dialog");
    }
//搜索逻辑
    //废弃
    void search(String string){
        WebView temp= mClist.getTop(0);
        temp.loadUrl(string);
        //getInfromation(webList_data.getTop());//载入后获取url和标题
        temp.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url){
                //getInfromation(webList_data.getTop());

            }
        });
        FrameLayout window1 =findViewById(R.id.Webview_group);
        window1.addView(temp);
    }


    /*public void multiplePage(View v){
        //已经在layout里设置了click属性，所以这里是点击时调用的函数
        Toast.makeText(MainActivity.this,"fuck",Toast.LENGTH_SHORT).show();
        //得调用viewControl();
        pagelist = findViewById(R.id.pagelist);
        //multflag的真触发显示和假来触发隐藏
        if(multflag){
        //multflag是真则隐藏网页，显示多窗口
            //pagelist是显示打开过网页的listview
            //TextView urlview = findViewById(R.id.url);

            webList_data.Top.t.setVisibility(View.INVISIBLE);//这条是设置现在显示的网页的可见性
            pagelist.setVisibility(View.VISIBLE);
            //urlview.setVisibility(View.INVISIBLE);
            multflag=false;

            adapter.notifyDataSetChanged();
            //要保持list和adapter的数据同步

        }else{
            pagelist.setVisibility(View.GONE);
            webList_data.Top.t.setVisibility(View.VISIBLE);
            multflag=true;

        }
    }//这里还缺一个在打开多窗口页面时处理返回键的方法，可以用multflag来做点事情
    */

/*
    private AdapterView.OnItemClickListener clickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            pagelist = findViewById(R.id.pagelist);
            pagelist.setVisibility(View.GONE);

           WebView web = webList_data.get(position);//listview单个条目
           web.setVisibility(View.VISIBLE);



           Toast.makeText(MainActivity.this,"fuck off",Toast.LENGTH_SHORT).show();
        }
    };//设置多窗口页面中单个条目的点击事件


    public void adapter(){
        adapter = new WebAdapter(MainActivity.this, webList_data);
        pagelist = findViewById(R.id.pagelist);
        pagelist.setAdapter(adapter);

    }
    public void show(){
        //多窗口页面中单个条目的点击事件，用来显示点击到的网页
        pagelist = findViewById(R.id.pagelist);
        pagelist.setOnItemClickListener(clickListener);

    }*/
/*
    public void back(){
        ImageButton imageButton=findViewById(R.id.backButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webList_data.Top.t.goBack();
            }
        });
    }
    public void home(){
        ImageButton home=findViewById(R.id.homebutton);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webList_data.Top.t.loadUrl("http://www.baidu.com");
                //addview1();//此行为测试加载特定layout用
            }
        });

    }*/

    //webview的设置
    void set1(WebView ti){

        ti.canGoBack();
        ti.canGoForward();
        WebSettings settings = ti.getSettings();
        // webview启用javascript支持 用于访问页面中的javascript
        settings.setJavaScriptEnabled(true);
        //设置WebView缓存模式 默认断网情况下不缓存
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        /**
         * LOAD_CACHE_ONLY: 不使用网络，只读取本地缓存数据
         * LOAD_DEFAULT: （默认）根据cache-control决定是否从网络上取数据。
         * LOAD_NO_CACHE: 不使用缓存，只从网络获取数据.
         * LOAD_CACHE_ELSE_NETWORK，只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
         */
        //断网情况下加载本地缓存
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        //让WebView支持DOM storage API
        settings.setDomStorageEnabled(true);
        //让WebView支持缩放
        settings.setSupportZoom(true);
        //启用WebView内置缩放功能
        settings.setBuiltInZoomControls(true);
        //让WebView支持可任意比例缩放
        settings.setUseWideViewPort(true);
        //设置WebView使用内置缩放机制时，是否展现在屏幕缩放控件上
        settings.setDisplayZoomControls(false);
        //设置在WebView内部是否允许访问文件
        settings.setAllowFileAccess(true);
        //设置WebView的访问UserAgent
        //settings.setUserAgentString(String string);
        //设置脚本是否允许自动打开弹窗
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        // 开启Application H5 Caches 功能
        settings.setAppCacheEnabled(true);
        // 设置编码格式
        settings.setDefaultTextEncodingName("utf-8");
        // 开启数据库缓存
        settings.setDatabaseEnabled(true);

    }
    /*
    public void addview1(){
        //测试把另外的layout加载进特定位置
        FrameLayout fragment_group=findViewById(R.id.fragment_group);
        LayoutInflater inflater = LayoutInflater.from(this);
        //获取layoutinflater，用它来加载视图，然后用addview把加载进来的视图放进特定位置。
        View view=inflater.inflate(R.layout.fragment_window,null,false);
        fragment_group.addView(view,1);
    }

    @Override
    public WebList getWebList_data(){
        return webList_data;
    }*/


    /*
    @Override
    protected void onResume() {
        WebView t = WebList.get(i).t;

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
        WebView t = WebList.get(i).t;
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
