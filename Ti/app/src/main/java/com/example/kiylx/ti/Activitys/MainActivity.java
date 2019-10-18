package com.example.kiylx.ti.Activitys;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.example.kiylx.ti.AboutHistory;
import com.example.kiylx.ti.model.WebViewManager;
import com.example.kiylx.ti.model.Converted_WebPage_Lists;
import com.example.kiylx.ti.model.CustomWebchromeClient;
import com.example.kiylx.ti.model.CustomWebviewClient;
import com.example.kiylx.ti.Fragments.MinSetDialog;
import com.example.kiylx.ti.Fragments.MultPage_DialogFragment;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.Fragments.Bookmark_Dialog;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements CustomWebviewClient.SETINFOS, MultPage_DialogFragment.MultPage_DialogF_interface {
    private static final String TAG="MainActivity";
    WebViewManager mWebViewManager;
    FrameLayout f1;
    Converted_WebPage_Lists mConverted_lists;
    static int currect=0;//静态变量，保存current的值，防止activity被摧毁时重置为0；
    private long mExitTime;//拿来判断按返回键间隔
    TextView m;
    AboutHistory sAboutHistory;
    private static final String CURRENT_URL ="current url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        f1=findViewById(R.id.Webview_group);
        mWebViewManager = WebViewManager.getInstance();
        sCUWL();

        if(mWebViewManager.isempty()){
            Log.d(TAG, "onCreate: isempty");
            addWebviewtohome();}else{
            f1.addView(mWebViewManager.getTop(currect));
        }/*当新进应用，是没有webview的，那么添加wevbview，否则，就把activity  stop()时remove的view加载回来*/
        toolbaract();
        Log.d("lifecycle","onCreate()");
        m=findViewById(R.id.search_edittext);
        useBookmarkPageActivityInterface();
        useMultPage_DialogFragmentInterface();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("lifecycle","onBookmarkt()");
/*
        sAboutHistory=AboutHistory.get(MainActivity.this);
        ArrayList<WebPage_Info> items= genItem();
        for (WebPage_Info i:items
        ) {
            sAboutHistory.addToDataBase(i);

        }*/
    }
    @Override
    protected void onResume(){
        super.onResume();
        int s= mWebViewManager.size();
        mWebViewManager.getTop(currect).onResume();
        Log.d("lifecycle","onResume()"+"webview数量"+s);

    }
    @Override
    protected void onPause(){
        super.onPause();
        Log.d("lifecycle","onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWebViewManager.getTop(currect).onPause();
        f1.removeAllViews();//移除所有视图
        Log.d("lifecycle","onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle","onDestroy()");
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        f1.addView(mWebViewManager.getTop(currect));
        Log.d("lifecycle","onReBookmarkt()");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Check if the key event was the Back button and if there's history
        //这里还要处理其他的返回事件,当返回true，事件就不再向下传递，也就是处理完这个事件就让别的再处理
        if((keyCode==KeyEvent.KEYCODE_BACK)&& mWebViewManager.getTop(currect).canGoBack()){
            mWebViewManager.getTop(0).goBack();
        }else{
            exit();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }
    private void exit(){
        if ((System.currentTimeMillis() - mExitTime) > 1000) {
            Toast.makeText(getApplicationContext(), "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis(); }
        else {
            //用户退出处理
            finish();
            System.exit(0); }
    }


    private void addWebviewtohome() {
        //作用是把数组第一个webview对象展示在面前
        newWebView(0);
        f1.addView(mWebViewManager.getTop(0));
    }
    @Override
    public void click_newPagebutton(){
        //新建标签页
        newTab();
    }
    @Override
    public void delete_page(int position){
        if(1== mWebViewManager.size()){
            //如果删除这个webview后没有其他的webview了，那就新建标签页
            mWebViewManager.getTop(0).loadUrl(null);
            return;
        }
        if(position>currect){
            mWebViewManager.destroy(position);
            delete_CUWL(position);
        }else if(position<currect){
            //把当前页面暂停并移除，然后加载新的currect处页面
            mWebViewManager.stop(currect);
            f1.removeView(mWebViewManager.getTop(currect));
            mWebViewManager.destroy(position);
            delete_CUWL(position);
            currect--;
            f1.addView(mWebViewManager.getTop(currect));
            mWebViewManager.reStart(currect);
        }else{
            if(position!= mWebViewManager.size()-1){
                //currect==position时，只要不是删除最后一个，就都这样操作：移除当前webview，删除webivew，把新提升上来的当前位置的webview添加进视图
                mWebViewManager.stop(currect);
                f1.removeView(mWebViewManager.getTop(position));
                mWebViewManager.destroy(position);
                delete_CUWL(position);
                f1.addView(mWebViewManager.getTop(position));
                mWebViewManager.reStart(currect);
            }else{
                mWebViewManager.stop(currect);
                f1.removeView(mWebViewManager.getTop(position));
                currect--;
                mWebViewManager.destroy(position);
                delete_CUWL(position);
                f1.addView(mWebViewManager.getTop(currect));
                mWebViewManager.reStart(currect);
            }
        }
    }
    @Override
    public void switchPage(int pos){
        //pos是指要切换到的页面
        mWebViewManager.stop(currect);
        f1.removeView(mWebViewManager.getTop(currect));
        f1.addView(mWebViewManager.getTop(pos));
        currect=pos;
        mWebViewManager.reStart(currect);
        setTextForbar(currect);//更新工具栏上的文字
    }
    @Override
    public int getCurrect(){
        return currect;
    }

    public void useBookmarkPageActivityInterface(){
        //来自BookmarkpageActivity，在点击某个收藏item后，让当前item加载收藏item的网址
        BookmarkPageActivity.setInterface(new BookmarkPageActivity.SatrPageA_interface(){
            @Override
            public void loadUrl(String urlname,boolean flags){
                if (flags){
                    newTab();
                    mWebViewManager.getTop(currect).loadUrl(urlname);
                }else
                mWebViewManager.getTop(currect).loadUrl(urlname);
            }
        });
    }

    public void useMultPage_DialogFragmentInterface(){
        MultPage_DialogFragment.setInterface(this);
    }

    void setTextForbar(int i) {
        //以下三行把工具栏的的文字更新
        //sCUWL();
        String mt = mConverted_lists.getTitle(i);
        m.setText(mt);
    }

    @Override
    public void setInfos(String title,String url){
        //网页加载完成时，更新存着所有打开的网页的list中当前页面的信息
        Log.d("lifecycle","webview标题"+url);
        m.setText(url);//更新工具栏上的文字
        //更新当前页面的webviewpageinfo信息
        updateInfo(title, url);
        //历史记录加入数据库
        sAboutHistory=AboutHistory.get(MainActivity.this);
        sAboutHistory.addToDataBase(mConverted_lists.getInfo(currect));

        String massage = mConverted_lists.getInfo(currect).getUrl();
        Log.d(TAG,"即将加入历史记录的内容"+massage);

    }
    private ArrayList<WebPage_Info> genItem() {
        ArrayList<WebPage_Info> tmp=new ArrayList<>();
        String[] datearr = new String[]{"2019-10-13","2019-10-06","2019-07-01","2019-09-08","2019-09-11"};
        for (int i=0;i<5;i++){
            tmp.add(new WebPage_Info("title"+i,"null",datearr[i]));
        }
        return tmp;
    }
            void updateInfo(String title, String url) {
                //将当前页面网址更新数据，并加入历史记录
                mConverted_lists.setTitle(currect,title);
                mConverted_lists.setUrl(currect,url);
                mConverted_lists.setdate(currect);
                //更改flags，标记为已载入网址
                mConverted_lists.setFlags(currect,1);
            }


            void sCUWL() {
        mConverted_lists = Converted_WebPage_Lists.get();
    }

    private void delete_CUWL(int i){
        //从Clist里删除了webview，sCurrentUse_webPage_lists也要保持一致
        mConverted_lists.delete(i);
    }

    private void newWebView(int i) {
        //新建webview并放进数组
        WebView web = new WebView(getApplicationContext());
        set1(web);
        //给new出来的webview执行设置
        web.setWebViewClient(new CustomWebviewClient(MainActivity.this));
        web.setWebChromeClient(new CustomWebchromeClient());
        mWebViewManager.addToFirst(web,i);
        //addToFirst(web,i)其实没有做限制，int i指示放在哪，默认是0，既是第一个位置。

        //空白页需要自定义，需要根据用户所写的来设置
        mConverted_lists.add("空白页","about:newTab","未分类",0);
        //一个新的空白的webview，title是“空白页”，url是“about:newTab”,flags是“未分类”
        //把网页信息保存进去，flags记为0，表示是一个newTab，不计入历史记录

    }
    public void newTab(){
        //由多窗口的新建主页按钮调用，作用是新建webview放进mclist的第0号位置，remove掉旧的webivew视图，刷新视图。
        mWebViewManager.stop(currect);
        f1.removeView(mWebViewManager.getTop(0));
        addWebviewtohome();
        currect=0;
        setTextForbar(currect);//更新工具栏上的文字
    }

    //工具栏设置
    private void toolbaract(){
        Toolbar bar = findViewById(R.id.toolbar1);
        setSupportActionBar(bar);
        //禁止显示标题
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //toolbar上的导航键
        /*bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebViewManager.getTop(currect).goBack();

            }
        });*/


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
                    case R.id.action_Bookmark:
                        Log.i(TAG, "onClick: 收藏按钮被触发");
                        showBookmarkDialog();
                        break;
                    /*case R.id.action_flash:
                        Log.i(TAG, "onClick: 刷新按钮被触发");
                        mWebViewManager.getTop(currect).reload();
                        break;*/
                    case R.id.action_menu:

                        Log.i(TAG, "onClick: 菜单按钮被触发");
                        minset();
                    default:
                        break;
                }
                return false;
            }
        });

    }

    public void showBookmarkDialog() {
        WebPage_Info tmp= mConverted_lists.getInfo(currect);
        FragmentManager fm = getSupportFragmentManager();
        Bookmark_Dialog dialog =Bookmark_Dialog.newInstance(1);
        dialog.putInfo(tmp);//把当前网页信息传给收藏dialog
        dialog.show(fm,"收藏当前网页");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }


    private void mult_dialog(){
        //展示多窗口
        FragmentManager fm = getSupportFragmentManager();
        MultPage_DialogFragment md=new MultPage_DialogFragment();
        md.show(fm,"fragment_multPage_dialog");
    }
    private void minset(){
        //底部设置界面
        FragmentManager fm =getSupportFragmentManager();
        MinSetDialog md=new MinSetDialog();
        md.show(fm,"minSetDialog");
    }

    //搜索框代码
    public void searchBar(View v){
        search_dialog();
    }
    private void search_dialog(){
        //展示搜索框
        Intent intent = new Intent(MainActivity.this, DoSearchActivity.class);
        intent.putExtra(CURRENT_URL, mWebViewManager.getTop(currect).getUrl());
        //把当前网页网址传进去
        startActivityForResult(intent,21);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_OK){
            return;
        }
        if(requestCode==21){
            //把DoSearchActivity的requestCode定义为21
            assert data != null;
            mWebViewManager.getTop(currect).loadUrl(data.getStringExtra("text_or_url"));
            //网页载入内容后把Webpage_InFo里元素的flags改为1，以此标志不是新标签页了
            //sCUWL();
            mConverted_lists.setFlags(currect,1);
            Log.d(TAG, "onActivityResult: 被触发" +data.getStringExtra("text_or_url"));
    }
    }

/*
    public void adapter(){
        adapter = new WebAdapter(MainActivity.this, webList_data);
        pagelist = findViewById(R.id.pagelist);
        pagelist.setAdapter(adapter);

    }
    */

    //webview的设置
    void set1(WebView ti){

        ti.canGoBack();
        ti.canGoForward();
        WebSettings settings = ti.getSettings();
        // webview启用javascript支持 用于访问页面中的javascript
        settings.setJavaScriptEnabled(true);
        //设置WebView缓存模式 默认断网情况下不缓存
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        /*
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
        settings.setUserAgentString(null);
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