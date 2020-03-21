package com.example.kiylx.ti.activitys;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.example.kiylx.ti.conf.PreferenceTools;
import com.example.kiylx.ti.conf.WebviewConf;
import com.example.kiylx.ti.core1.WebViewInfo_Manager;
import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.databinding.ActivityMainBinding;
import com.example.kiylx.ti.downloadCore.DownloadServices;
import com.example.kiylx.ti.downloadFragments.DownloadWindow;
import com.example.kiylx.ti.myInterface.ActionSelectListener;
import com.example.kiylx.ti.myInterface.DownloadInterfaceImpl;
import com.example.kiylx.ti.myInterface.MultiDialog_Functions;
import com.example.kiylx.ti.myInterface.OpenOneWebpage;
import com.example.kiylx.ti.core1.WebViewManager;
import com.example.kiylx.ti.myFragments.MinSetDialog;
import com.example.kiylx.ti.myFragments.MultPage_DialogFragment;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.myInterface.SearchTextOnWebview;
import com.example.kiylx.ti.myInterface.Setmessage;
import com.example.kiylx.ti.Tool.ProcessUrl;

import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements MultiDialog_Functions {
    private static final String TAG = "MainActivity";
    private static final String CURRENT_URL = "current url";

    static int currect = 0;//静态变量，保存current的值，防止activity被摧毁时重置为0；
    private long mExitTime;//拿来判断按返回键间隔
    private Boolean isOpenedSearchText = false;//用来指示在webview页面上文本搜索有没有展开，按下返回键时如果这个是true，就把文本搜索收起来

    private WebViewManager mWebViewManager;
    private WebViewInfo_Manager mConverted_lists;//存储webpage_info的list
    public DownloadServices.DownloadBinder mDownloadBinder;
    public DownloadInfo downloadInfo;//下载信息

    private FrameLayout f1;
    private TextView mTextView;//主界面的工具栏里的搜索框
    private ActivityMainBinding mainBinding;//用于更新搜索框标题的databinding
    private View inflated;//搜索webview文字的搜索框
    private MultPage_DialogFragment md;//多窗口dialogFragment

    //权限
    String[] allperm = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstInstall();//判断是不是第一次安装
        setContentView(R.layout.activity_main);

        //获取类的实例
        mWebViewManager = WebViewManager.getInstance(MainActivity.this);
        //获取Converted_Webpage_List,并传入mWebViewManager注册观察者
        mConverted_lists = WebViewInfo_Manager.get(mWebViewManager);

        //初次设置偏好值
        //initSefaultValues();
        //监听默认值的改变
        //getDefaultValues();

        //实例化某些view
        f1 = findViewById(R.id.Webview_group);
        mTextView = findViewById(R.id.search_edittext);

        //判断webviewmanager中有没有webview，有的话执行恢复方法，把webview重新放进屏幕。当新进应用，是没有webview的，那么添加wevbview，否则，就把activity  stop()时remove的view加载回来
        if (mWebViewManager.isempty()) {
            Log.d(TAG, "onCreate: isempty");
            newTab();
        } else {
            mWebViewManager.getTop(currect).onResume();

            //注：获取当前webview的父视图，然后再把这个webview从父视图中移除，然后再重新添加，已解决白屏问题。
            WebView webView = mWebViewManager.getTop(currect);
            if (webView.getParent() != null) {
                ((ViewGroup) webView.getParent()).removeView(webView);
            }
            f1.addView(webView);
        }

        //工具栏
        toolbaract();
        Log.d("lifecycle", "onCreate()");

        //接口回调
        openwebpage_fromhistoryORbookmark();
        useMultPage_DialogFragmentInterface();
        downloadWindow_startDownload();

        mWebViewManager.setInterface(new Setmessage() {
            @Override
            public void setInfos() {
                //网页加载完成后会通过这里更新底栏的文字
                setTextForbar(currect);
            }

        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mWebViewManager.getTop(currect).onResume();
        //f1.addView(mWebViewManager.getTop(currect));
        Log.d("lifecycle", "onReStart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("lifecycle", "onStart'");
    }

    @Override
    protected void onResume() {
        super.onResume();
        int s = mWebViewManager.size();
        Log.d("lifecycle", "onResume()" + "webview数量" + s);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("lifecycle", "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWebViewManager.getTop(currect).onPause();
        //f1.removeAllViews();//移除所有视图
        Log.d("lifecycle", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //-unbindService(connection);
        Log.d("lifecycle", "onDestroy()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Check if the key event was the Back button and if there's history
        //这里还要处理其他的返回事件,当返回true，事件就不再向下传递，也就是处理完这个事件就让别的再处理
        if (keyCode == KeyEvent.KEYCODE_BACK && isOpenedSearchText) {
            //先处理webview的文本搜索
            closeSearchText();
        } else {
            //处理在没有文本搜索的时候
            if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebViewManager.getTop(currect).canGoBack()) {
                mWebViewManager.getTop(currect).goBack();
            } else {
                exit();
                return true;
            }
        }

        return true;
    }

    /**
     * 第一次安装后启动，写入一个preference，判断是不是第一次打开
     * 若是，打开启动页，并关闭mainactivity，在startpageactivity中作出一系列的初始化操作
     */
    private void firstInstall() {

        if (!PreferenceTools.getBoolean(this, "Installed")) {
            //如果是第一次打开应用Installed不存在，默认拿到false。则可以在这里做一些初始化操作。之后在StartPageActivity中写入Installed为true。
            Intent intent = new Intent(MainActivity.this, StartPageActivity.class);
            startActivity(intent);//打开启动页activity
            finish();//结束mainactivity
        }

    }

    private void exit() {
        if ((System.currentTimeMillis() - mExitTime) > 1000) {
            Toast.makeText(getApplicationContext(), "再按一次退出应用", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();
        } else {
            //用户退出处理
            finish();
            System.exit(0);
        }
    }

    /**
     * 退出应用或是其他原因使webview从屏幕上被移除，那就用这个方法恢复
     */
    private void reasumeWebview() {
    }

    /*
     * @param i pos，webview要添加进的位置
     *          新建一个webview并放进WebViewManager
     *
       private void newWebView(int i) {
        //新建webview并放进数组
        WebView web = new WebView(getApplicationContext());
        set1(web);
        //给new出来的webview执行设置
        web.setWebViewClient(new CustomWebviewClient(MainActivity.this));
        web.setWebChromeClient(new CustomWebchromeClient());
        mWebViewManager.addInWebManager(web, i, 0);

    }*/


    @Override
    public void click_newPagebutton() {
        //新建标签页
        newTab();
    }

    /**
     * 读取preference中的是否自定义主页，并且处理获取到的自定义主页网址。
     */
    public void newTab() {
        //是否使用自定义的主页
        String home_url = null;
        if (PreferenceTools.getBoolean(this, WebviewConf.useCustomHomepage)) {//条件true时获取自定义网址，是false时则使用默认主页
            home_url = PreferenceTools.getString(this, WebviewConf.homepageurl);
            //补全网址，以及如果没有填写任何字符，使用默认主页
            if (home_url.equals("")) {
                home_url = SomeRes.default_homePage_url;
            } else {
                home_url = ProcessUrl.converKeywordLoadOrSearch(home_url);
            }
        }
        //由多窗口的新建主页按钮调用，作用是新建webview放进mclist的第0号位置，remove掉旧的webivew视图，刷新视图。
        if (!mWebViewManager.isempty()) {
            mWebViewManager.stop(currect);
            f1.removeView(mWebViewManager.getTop(currect));
            currect = 0;
        }

        mWebViewManager.newWebView(0, getApplicationContext(), MainActivity.this);
        mWebViewManager.getTop(currect).setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String title, String selectText) {
                Toast.makeText(MainActivity.this, title + selectText, Toast.LENGTH_LONG).show();
            }
        });

        f1.addView(mWebViewManager.getTop(currect));

        mWebViewManager.loadHomePage(-1, home_url);//新建标签页载入主页
    }

    @Override
    public void delete_page(int position) {
        if (1 == mWebViewManager.size()) {
            //如果删除这个webview后没有其他的webview了，那就新建标签页
            mWebViewManager.getTop(0).loadUrl(SomeRes.default_homePage_url);
            md.dismiss();//关闭多窗口
            return;
        }
        if (position > currect) {
            mWebViewManager.destroy(position);

        } else if (position < currect) {
            //把当前页面暂停并移除，然后加载新的currect处页面
            mWebViewManager.stop(currect);
            f1.removeView(mWebViewManager.getTop(currect));
            mWebViewManager.destroy(position);

            currect--;
            f1.addView(mWebViewManager.getTop(currect));
            mWebViewManager.reStart(currect);
        } else {//position==currect的情况
            if (position != mWebViewManager.size() - 1) {
                //当前位置或者说position位置在webview的列表中不是最后一个。
                /*删除的位置是当前位置，但删除的位置不是最后一个，
                所以，直接移除视图，删除webview，
                这时后面一个webview在webviewmanager的arraylist中会被向前移动，
                这时直接添加上当前位置的webview（这个位置没变，但指代的已经是被删除的webview后面的webview）视图*/
                mWebViewManager.stop(currect);
                f1.removeView(mWebViewManager.getTop(position));
                mWebViewManager.destroy(position);
                //delete_CUWL(position);
                f1.addView(mWebViewManager.getTop(position));
                mWebViewManager.reStart(currect);
            } else {
                //当前位置或者说position位置在webview的列表中是最后一个。
                /*删除当前webview和视图，把列表中被删除webview前面的视图挪到屏幕上*/
                mWebViewManager.stop(currect);
                f1.removeView(mWebViewManager.getTop(position));
                currect--;
                mWebViewManager.destroy(position);
                //delete_CUWL(position);
                f1.addView(mWebViewManager.getTop(currect));
                mWebViewManager.reStart(currect);
            }
        }
    }

    @Override
    public void switchPage(int pos) {
        //pos是指要切换到的页面
        mWebViewManager.stop(currect);
        f1.removeView(mWebViewManager.getTop(currect));
        f1.addView(mWebViewManager.getTop(pos));
        currect = pos;
        mWebViewManager.reStart(currect);
        setTextForbar(currect);//更新工具栏上的文字
    }

    /**
     * @return 获取当前webview在arraylist中的位置
     */
    public static int getCurrect() {
        return currect;
    }

    /**
     * 在点击某个收藏记录或是历史记录后，让当前webview或新的webview加载网址
     */
    public void openwebpage_fromhistoryORbookmark() {
        BookmarkPageActivity.setInterface(new OpenOneWebpage() {
            @Override
            public void loadUrl(String urlname, boolean flags) {
                if (flags) {
                    newTab();
                    mWebViewManager.getTop(currect).loadUrl(urlname);
                } else
                    mWebViewManager.getTop(currect).loadUrl(urlname);

            }
        });
        HistoryActivity.setInterface(new OpenOneWebpage() {
            @Override
            public void loadUrl(String urlname, boolean flags) {
                if (flags) {
                    newTab();
                    mWebViewManager.getTop(currect).loadUrl(urlname);
                } else
                    mWebViewManager.getTop(currect).loadUrl(urlname);

            }
        });

    }

    public void useMultPage_DialogFragmentInterface() {
        MultPage_DialogFragment.setInterface(this);
    }

    /**
     * @param i 当前webview在列表中的位置
     *          <p>
     *          工具栏的的文字更新，获取当前浏览网页的标题，设置到底栏
     */
    void setTextForbar(int i) {
        String mt = mConverted_lists.getUrl(i);
        if (mt.equals(SomeRes.default_homePage_url)) {
            //不显示默认主页的url
            mt = "";
        }
        mTextView.setText(mt);
    }
/*
//测试方法
    private ArrayList<WebPage_Info> genItem() {
        ArrayList<WebPage_Info> tmp = new ArrayList<>();
        String[] datearr = new String[]{"2019-10-13", "2019-10-06", "2019-07-01", "2019-09-08", "2019-09-11"};
        for (int i = 0; i < 5; i++) {
            tmp.add(new WebPage_Info("title" + i, "null", datearr[i]));
        }
        return tmp;
    }*/


    //工具栏设置
    private void toolbaract() {
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
                switch (item.getItemId()) {
                    case R.id.action_mult:
                        Log.i(TAG, "onClick: 多窗口按钮被触发");
                        mult_dialog();
                        break;
                    /*case R.id.action_Bookmark:
                        Log.i(TAG, "onClick: 收藏按钮被触发");
                        showBookmarkDialog();
                        break;
                    case R.id.action_flash:
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
/*
    public void showBookmarkDialog() {
        WebPage_Info tmp = mConverted_lists.getInfo(currect);
        FragmentManager fm = getSupportFragmentManager();
        Bookmark_Dialog dialog = Bookmark_Dialog.newInstance(1);
        dialog.putInfo(tmp);//把当前网页信息传给收藏dialog
        dialog.show(fm, "收藏当前网页");
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    /**
     * 展示多窗口
     */
    private void mult_dialog() {

        FragmentManager fm = getSupportFragmentManager();
        md = new MultPage_DialogFragment();
        md.show(fm, "fragment_multPage_dialog");
    }

    /**
     * 底部设置界面
     */
    private void minset() {

        FragmentManager fm = getSupportFragmentManager();
        MinSetDialog md = MinSetDialog.newInstance(mConverted_lists.getInfo(currect));
        md.setInterafce(new SearchTextOnWebview() {
            @Override
            public void search() {
                //打开webview搜索文本
                openSearchText();
            }
        });
        md.show(fm, "minSetDialog");
    }

    /**
     * 搜索框代码,由activity_main.xml中的搜索框调用
     */
    public void searchBar(View v) {
        search_dialog();
    }

    /**
     * 展示搜索框
     */
    private void search_dialog() {

        Intent intent = new Intent(MainActivity.this, DoSearchActivity.class);
        intent.putExtra(CURRENT_URL, mWebViewManager.getTop(currect).getUrl());
        //把当前网页网址传进去
        startActivityForResult(intent, 21);


    }

    /**
     * 把webview上的文本搜索收起来
     */
    private void closeSearchText() {
        inflated.setVisibility(View.INVISIBLE);
        isOpenedSearchText = false;
        mWebViewManager.clearMatches(currect);
    }

    /**
     * 打开文本搜索功能
     */
    public void openSearchText() {

        ViewStub stub = findViewById(R.id.viewStub_search);
        if (stub != null) {
            inflated = stub.inflate();
        }
        inflated.setVisibility(View.VISIBLE);
        isOpenedSearchText = true;

        EditText editText = inflated.findViewById(R.id.textView_search);
        ImageButton goaHead = inflated.findViewById(R.id.goahead);
        ImageButton back = inflated.findViewById(R.id.back);

        editText.setText("");//清空文本框内容
        goaHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebViewManager.goaHead(currect);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebViewManager.text_back(currect);
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String content = s.toString();
                if (!TextUtils.isEmpty(content)) {
                    mWebViewManager.findAllAsync(currect, content);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 21) {
            //把DoSearchActivity的requestCode定义为21
            assert data != null;
            mWebViewManager.getTop(currect).loadUrl(data.getStringExtra("text_or_url"));
            //网页载入内容后把Webpage_InFo里元素的flags改为1，以此标志不是新标签页了
            //mConverted_lists.setWEB_feature_1(currect, 1);
            Log.d(TAG, "onActivityResult: 被触发" + data.getStringExtra("text_or_url"));
        }
    }

    /**
     * 设置下载接口。
     * 让开始下载任务的dialog可以调用这个方法来开启并绑定服务。
     */
    private void downloadWindow_startDownload() {
        DownloadWindow.setMinterface(new DownloadInterfaceImpl() {

            @Override
            public void startDownoadService(DownloadInfo info) {

                //如果有存储权限，则可以开始下载，否则告诉用户申请权限
                if (EasyPermissions.hasPermissions(MainActivity.this, allperm)) {
                    //Toast.makeText(MainActivity.this,"已赋予权限",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, DownloadServices.class);
                    startService(intent);
                    bindService(intent, connection, BIND_AUTO_CREATE);
                    downloadInfo = info;
                } else {
                    EasyPermissions.requestPermissions(MainActivity.this, "没有文件读写权限，请去设置给予权限后再试", 20033, allperm);
                }



            }
        });
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mDownloadBinder = (DownloadServices.DownloadBinder) service;
            mDownloadBinder.startDownload(downloadInfo);//绑定服务，开始下载
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
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