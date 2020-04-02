package com.example.kiylx.ti.activitys;

import android.Manifest;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import com.example.kiylx.ti.Tool.SavePNG_copyText;
import com.example.kiylx.ti.conf.PreferenceTools;
import com.example.kiylx.ti.conf.WebviewConf;
import com.example.kiylx.ti.core1.WebViewInfo_Manager;
import com.example.kiylx.ti.corebase.DownloadInfo;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.corebase.WebPage_Info;
import com.example.kiylx.ti.databinding.ActivityMainBinding;
import com.example.kiylx.ti.downloadCore.DownloadServices;
import com.example.kiylx.ti.downloadFragments.DownloadDialog;
import com.example.kiylx.ti.model.EventMessage;
import com.example.kiylx.ti.myFragments.Bookmark_Dialog;
import com.example.kiylx.ti.myFragments.MultPage_Dialog;
import com.example.kiylx.ti.myInterface.ActionSelectListener;
import com.example.kiylx.ti.myInterface.ControlWebView;
import com.example.kiylx.ti.myInterface.DownloadInterfaceImpl;
import com.example.kiylx.ti.myInterface.HandleClickedLinks;
import com.example.kiylx.ti.myInterface.MultiDialog_Functions;
import com.example.kiylx.ti.myInterface.OpenOneWebpage;
import com.example.kiylx.ti.core1.WebViewManager;
import com.example.kiylx.ti.myFragments.MinSetDialog;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.Tool.ProcessUrl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements MultiDialog_Functions {
    private static final String TAG = "MainActivity";
    private static final String CURRENT_URL = "current url";

    public static int current = 0;//静态变量，保存current的值，防止activity被摧毁时重置为0；
    private long mExitTime;//拿来判断按返回键间隔
    private Boolean isOpenedSearchText = false;//用来指示在webview页面上文本搜索有没有展开，按下返回键时如果这个是true，就把文本搜索收起来
    private String WEBTITLE ="webTitle";//用来存放当前标签页的网址，在旋转屏幕时能恢复下方搜索栏上的文字
    private String CURRENTINT="currentInt";//用来存放指示当前webview的位置

    private WebViewManager mWebViewManager;
    private WebViewInfo_Manager mConverted_lists;//存储webpage_info的list
    public DownloadServices.DownloadBinder mDownloadBinder;
    public DownloadInfo downloadInfo;//下载信息

    private FrameLayout f1;
    private TextView mTextView;//主界面的工具栏里的搜索框
    private ActivityMainBinding mainBinding;//用于更新搜索框标题的databinding
    private View inflated;//搜索webview文字的搜索框
    private MultPage_Dialog md;//多窗口dialogFragment
    private HandleClickedLinks handleClickedLinks;
    private ControlWebView controlInterface;
    private static boolean seviceBund = false;//绑定服务时把它改为true；

    //权限
    String[] allperm = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firstInstall();//判断是不是第一次安装
        setContentView(R.layout.activity_main);

        //实现处理webview长按事件的接口
        achieveHandlerClickInterface();
        //实现控制webview的接口
        implControlWebview();
        //获取WebViewManager的实例
        mWebViewManager = WebViewManager.getInstance(MainActivity.this, handleClickedLinks);

        //获取Converted_Webpage_List,并传入mWebViewManager注册观察者
        mConverted_lists = WebViewInfo_Manager.get(mWebViewManager);

        //实例化某些view
        f1 = findViewById(R.id.Webview_group);
        mTextView = findViewById(R.id.search_edittext);

        //判断webviewmanager中有没有webview，有的话执行恢复方法，把webview重新放进屏幕。当新进应用，是没有webview的，那么添加wevbview，否则，就把activity  stop()时remove的view加载回来
        if (mWebViewManager.isempty()) {
            Log.d(TAG, "onCreate: isempty");
            newTab(null);
        } else {
            mWebViewManager.getTop(current).onResume();

            //注：获取当前webview的父视图，然后再把这个webview从父视图中移除，然后再重新添加，已解决白屏问题。
            WebView webView = mWebViewManager.getTop(current);
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
        downloadDialog_startDownload();

        if (savedInstanceState!=null){
            mTextView.setText(savedInstanceState.getString(WEBTITLE));
            current=savedInstanceState.getInt(CURRENTINT);
        }
    }

    /**
     * 接受网页加载完成信息，重新获取数据更新界面,以及倒掉被删除的webview垃圾
     *
     * @param massage
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateData(EventMessage massage) {
        switch (massage.getType()) {
            case 2:
                setTextForbar(current);
                break;
            case 3:
                mWebViewManager.throwTrash();
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mWebViewManager.getTop(current).onResume();
        //f1.addView(mWebViewManager.getTop(current));
        Log.d("lifecycle", "onReStart()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("lifecycle", "onStart'");
        EventBus.getDefault().register(this);
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
        mWebViewManager.getTop(current).onPause();
        //f1.removeAllViews();//移除所有视图
        EventBus.getDefault().unregister(this);
        Log.d("lifecycle", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (seviceBund)
            unbindService(connection);
        Log.d("lifecycle", "onDestroy()");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(WEBTITLE,mTextView.getText().toString());
        outState.putInt(CURRENTINT,current);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Check if the key event was the Back button and if there's history
        //这里还要处理其他的返回事件,当返回true，事件就不再向下传递，也就是处理完这个事件就让别的再处理
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isOpenedSearchText){
                //先处理webview的文本搜索
                closeSearchText();
            }else if (mWebViewManager.getTop(current).canGoBack()) {//处理在没有文本搜索的时候
                mWebViewManager.getTop(current).goBack();
            } else {
                exit();
                return true;
            }
        }

        return false;
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


    @Override
    public void click_newPagebutton() {
        //新建标签页
        newTab(null);
    }


    /**
     * 读取preference中的是否自定义主页，并且处理获取到的自定义主页网址。
     * 传入的参数是null时，获取默认的主页，然后新建标签页，并载入默认主页
     * 传入的参数不是null时，新建标签页，并载入传入的网址
     */
    public void newTab(String url) {
        String home_url = null;
        if (url == null) {
            //条件true时获取自定义网址，是false时则使用默认主页
            if (PreferenceTools.getBoolean(this, WebviewConf.useCustomHomepage)) {
                home_url = PreferenceTools.getString(this, WebviewConf.homepageurl);
                //补全网址，以及如果开了自定义网址，但是没有填写任何字符，也使用默认主页
                if (home_url.equals("")) {
                    home_url = SomeRes.default_homePage_url;
                } else {
                    home_url = ProcessUrl.converKeywordLoadOrSearch(home_url);
                }
            }
        } else {
            //传入参数不是null
            home_url = url;
        }

        //由多窗口的新建主页按钮调用，作用是新建webview放进mclist的最后的位置，remove掉旧的webivew视图，刷新视图。
        if (!mWebViewManager.isempty()) {
            mWebViewManager.stop(current);
            f1.removeView(mWebViewManager.getTop(current));
            current = mWebViewManager.size();
        }

        mWebViewManager.newWebView(current, getApplicationContext(), MainActivity.this);
        mWebViewManager.getTop(current).setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String title, String selectText) {
                Toast.makeText(MainActivity.this, title + selectText, Toast.LENGTH_LONG).show();
            }
        });

        f1.addView(mWebViewManager.getTop(current));

        mWebViewManager.loadHomePage(current, home_url);//新建标签页载入主页，新建标签页默认插入最后的位置。
    }

    /**
     * @param url 网址
     *            后台打开网页使用此方法
     */
    public void newTabInBackground(String url) {
        mWebViewManager.newWebView(current + 1, getApplicationContext(), MainActivity.this);
        mWebViewManager.getTop(current + 1).setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String title, String selectText) {
                Toast.makeText(MainActivity.this, title + selectText, Toast.LENGTH_LONG).show();
            }
        });
        mWebViewManager.loadHomePage(current + 1, url);
    }

    @Override
    public void delete_page(int position) {
        deleteWebview(position);
    }

    public void deleteWebview(int position) {
        if (mWebViewManager.size() == 1) {
            //String url = PreferenceTools.getString(MainActivity.this, WebviewConf.homepageurl);
            //mWebViewManager.wash(position, url);
            f1.removeView(mWebViewManager.getTop1(position));
            mWebViewManager.removeToTrash(position, false);
            click_newPagebutton();
            return;
        }
        if (position < current) {
            mWebViewManager.removeToTrash(position, true);
            current -= 1;
        } else if (position == current) {
            /*
             * pos和current相等，也就是要删除的webview就是当前浏览的webview，那先把pos位置的webview从f1中移除。
             * 1.如果是最后一个元素，把前一个元素添加到f1，然后把current减一
             * 2.如果是倒数第二个元素或者说不是会有一个元素，把后面的元素添加到f1，current不用改
             * <p>
             * 再然后调用removeToTrash()，把webview移动到垃圾桶
             * */
            f1.removeView(mWebViewManager.getTop(position));
            if (position == mWebViewManager.size() - 1) {//列表中最后面的元素
                f1.addView(mWebViewManager.getTop(position - 1));
                current -= 1;
                mWebViewManager.reStart(current);
            } else {
                f1.addView(mWebViewManager.getTop(position + 1));
                mWebViewManager.reStart(position + 1);
            }
            mWebViewManager.removeToTrash(position, false);
        } else {//pos>current的情况
            mWebViewManager.removeToTrash(position, true);
        }
        setTextForbar(current);//更新标题栏
    }

    @Override
    public void switchPage(int pos) {
        //pos是指要切换到的页面
        mWebViewManager.stop(current);
        f1.removeView(mWebViewManager.getTop(current));
        f1.addView(mWebViewManager.getTop(pos));
        current = pos;
        mWebViewManager.reStart(current);
        setTextForbar(current);//更新工具栏上的文字
    }

    /**
     * @return 获取当前webview在arraylist中的位置
     */
    public static int getCurrent() {
        return current;
    }

    /**
     * 在点击某个收藏记录或是历史记录后，让当前webview或新的webview加载网址
     */
    public void openwebpage_fromhistoryORbookmark() {
        BookmarkPageActivity.setInterface(new OpenOneWebpage() {
            @Override
            public void loadUrl(String url, boolean flags) {
                if (flags) {
                    newTab(url);
                    // mWebViewManager.getTop(current).loadUrl(url);
                } else
                    mWebViewManager.getTop(current).loadUrl(url);

            }
        });
        HistoryActivity.setInterface(new OpenOneWebpage() {
            @Override
            public void loadUrl(String url, boolean flags) {
                if (flags) {
                    newTab(url);
                    //mWebViewManager.getTop(current).loadUrl(url);
                } else
                    mWebViewManager.getTop(current).loadUrl(url);

            }
        });

    }

    public void useMultPage_DialogFragmentInterface() {
        MultPage_Dialog.setInterface(this);
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
                mWebViewManager.getTop(current).goBack();

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
                        mWebViewManager.getTop(current).reload();
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

    /**
     * @param menu
     * @return <p>
     * optionMenu
     */
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
        md = new MultPage_Dialog();
        md.show(fm, "fragment_multPage_dialog");
    }

    /**
     * 底部设置界面
     */
    private void minset() {

        FragmentManager fm = getSupportFragmentManager();
        MinSetDialog md = MinSetDialog.newInstance(mConverted_lists.getInfo(current));
        md.setInterafce(controlInterface);//控制网页的一些方法交给MinSetDialog
        md.show(fm, "minSetDialog");
    }

    /**
     * 搜索框代码,由activity_main.xml中的搜索框调用
     */
    public void searchBar(View v) {
        search_dialog();
    }

    /**
     * 展示搜索框,输入文本，载入网页
     */
    private void search_dialog() {

        Intent intent = new Intent(MainActivity.this, DoSearchActivity.class);
        intent.putExtra(CURRENT_URL, mWebViewManager.getTop(current).getUrl());
        //把当前网页网址传进去
        startActivityForResult(intent, 21);
    }

    /**
     * 把webview的页内文本搜索收起来
     */
    private void closeSearchText() {
        inflated.setVisibility(View.INVISIBLE);
        isOpenedSearchText = false;
        mWebViewManager.clearMatches(current);
    }

    /**
     * 打开页内文本搜索功能
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
                mWebViewManager.goaHead(current);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebViewManager.text_back(current);
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
                    mWebViewManager.findAllAsync(current, content);
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
            mWebViewManager.getTop(current).loadUrl(data.getStringExtra("text_or_url"));
            //网页载入内容后把Webpage_InFo里元素的flags改为1，以此标志不是新标签页了
            //mConverted_lists.setWEB_feature_1(current, 1);
            Log.d(TAG, "onActivityResult: 被触发" + data.getStringExtra("text_or_url"));
        }
    }

    /**
     * 检查下载所需的权限，并实现下载接口，以及开启下载服务
     * 让开始下载任务的dialog调用这个方法来开启并绑定服务，开始下载。
     */
    private void downloadDialog_startDownload() {
        DownloadDialog.setMinterface(new DownloadInterfaceImpl() {

            @Override
            public void startDownoadService(DownloadInfo info) {

                //如果有存储权限，则可以开始下载，否则告诉用户申请权限
                if (EasyPermissions.hasPermissions(MainActivity.this, allperm)) {
                    //Toast.makeText(MainActivity.this,"已赋予权限",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, DownloadServices.class);
                    startService(intent);
                    bindService(intent, connection, BIND_AUTO_CREATE);
                    seviceBund = true;
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
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 实现处理webview长按事件处理的接口，把接口传入webViewManager，
     * 由WebViewManager在new CustomAWebView后调用CustomAWebView 的 setHandleClickLinks()方法实现处理
     */
    private void achieveHandlerClickInterface() {
        if (handleClickedLinks == null) {
            handleClickedLinks = new HandleClickedLinks() {
                SavePNG_copyText png_copyText= new SavePNG_copyText(MainActivity.this);
                @Override
                public void onImgSelected(int x, int y, int type, String extra) {
                    Log.d(TAG, "onImgSelected: " + extra);
                    String[] menu = new String[]{"复制图片链接地址", "新窗口打开图片", "分享图片", "保存图片"};
                    new AlertDialog.Builder(MainActivity.this).setItems(menu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    //复制链接地址
                                    Toast.makeText(MainActivity.this, "点了链接：" + extra, Toast.LENGTH_LONG).show();
                                    //clipData(extra);
                                    png_copyText.clipData(extra);
                                    break;
                                case 1:
                                    //新窗口打开
                                    newTab(extra);
                                    break;
                                case 2:
                                    //分享图片
                                    break;
                                case 3:
                                    //"保存图片"
                                    //saveImage(extra);
                                    png_copyText.savePic(extra);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;

                            }
                            dialog.dismiss();
                        }
                    }).show();
                }

                @Override
                public void onLinkSelected(int x, int y, int type, String extra) {
                    Log.d(TAG, "onLinkSelected: " + extra);
                    String[] menu = new String[]{"复制链接地址", "新窗口打开", "分享", "后台打开", "添加到书签"};
                    new AlertDialog.Builder(MainActivity.this).setItems(menu, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    //复制链接地址
                                    Toast.makeText(MainActivity.this, "点了链接：" + extra, Toast.LENGTH_LONG).show();
                                    //clipData(extra);
                                    png_copyText.clipData(extra);
                                    break;
                                case 1:
                                    //新窗口打开
                                    newTab(extra);
                                    break;
                                case 2:
                                    //分享
                                    controlInterface.sharing(extra);
                                    break;
                                case 3:
                                    //后台打开
                                    newTabInBackground(extra);
                                    break;
                                case 4:
                                    //添加到书签
                                    controlInterface.addtobookmark(extra);
                                    break;

                            }
                            dialog.dismiss();
                        }
                    }).show();
                }

                @Override
                public void onImgLink(int touchX, int touchY, int type, String extra) {

                }

            };
        }
    }


    /**
     * 实现一些控制网页的方法，用于minsetDialog或者长按菜单
     */
    private void implControlWebview() {
        if (controlInterface == null) {
            controlInterface = new ControlWebView() {
                @Override
                public void sharing(String url) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_TEXT, url);
                    i.putExtra(Intent.EXTRA_SUBJECT, "网址");
                    startActivity(i);
                }

                @Override
                public void addtobookmark(String url) {
                    FragmentManager fm = getSupportFragmentManager();
                    //把当前网页信息传给收藏dialog
                    Bookmark_Dialog dialog = Bookmark_Dialog.newInstance(1, mConverted_lists.getInfo(current));
                    dialog.show(fm, "收藏当前网页");
                }

                @Override
                public void reload() {
                    mWebViewManager.reLoad((MainActivity.this));
                }

                @Override
                public void searchText() {
                    //打开搜索webview内的文本
                    openSearchText();
                }

                @Override
                public void usePcMode() {
                    mWebViewManager.reLoad_pcmode();
                }

                @Override
                public void newPage(String url) {
                    newTab(url);
                }
            };
        }

    }

}
/*
*
* View decorView = getActivity().getWindow().getDecorView();
                //隐藏NavigationBar
                    int option = SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                    decorView.setSystemUiVisibility(option);
* */