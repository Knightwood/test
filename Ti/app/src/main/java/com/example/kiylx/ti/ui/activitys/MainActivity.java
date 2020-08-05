package com.example.kiylx.ti.ui.activitys;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.xapplication.Xapplication;
import com.example.kiylx.ti.mvp.contract.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.presenter.lifecycles.MainLifeCycleObserver;
import com.example.kiylx.ti.ui.base.BaseActivity;
import com.example.kiylx.ti.webview32.CustomAWebView;
import com.example.kiylx.ti.interfaces.WebViewChromeClientInterface;
import com.example.kiylx.ti.tool.preferences.DefaultPreferenceTool;
import com.example.kiylx.ti.tool.SomeTools;
import com.example.kiylx.ti.tool.preferences.PreferenceTools;
import com.example.kiylx.ti.mvp.presenter.WebViewInfo_Manager;
import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.downloadpack.downloadcore.DownloadServices;
import com.example.kiylx.ti.downloadpack.fragments.DownloadDialog;
import com.example.kiylx.ti.model.EventMessage;
import com.example.kiylx.ti.tool.dateProcess.TimeProcess;
import com.example.kiylx.ti.ui.fragments.Bookmark_Dialog;
import com.example.kiylx.ti.ui.fragments.MultPage_Dialog;
import com.example.kiylx.ti.interfaces.ActionSelectListener;
import com.example.kiylx.ti.interfaces.ControlWebView;
import com.example.kiylx.ti.downloadpack.dinterface.DownloadInterfaceImpl;
import com.example.kiylx.ti.interfaces.HandleClickedLinks;
import com.example.kiylx.ti.interfaces.MultiDialog_Functions;
import com.example.kiylx.ti.interfaces.OpenOneWebpage;
import com.example.kiylx.ti.mvp.presenter.WebViewManager;
import com.example.kiylx.ti.ui.fragments.MinSetDialog;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.tool.ProcessUrl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends BaseActivity implements MultiDialog_Functions, WebViewManager.UpdateProgress {
    private static final String TAG = "MainActivity";
    private static final String OPENED_EDIT = "opened_edit";

    public static int current = 0;//静态变量，保存current的值，防止activity被摧毁时重置为0；
    private long mExitTime;//拿来判断按返回键间隔
    private Boolean isOpenedSearchText = false;//用来指示在webview页面上文本搜索有没有展开，按下返回键时如果这个是true，就把文本搜索收起来
    private String WEBTITLE = "webTitle";//用来存放当前标签页的网址，在旋转屏幕时能恢复下方搜索栏上的文字
    private String CURRENTINT = "currentInt";//用来存放指示当前webview的位置

    private WebViewManager mWebViewManager;
    private WebViewInfo_Manager mConverted_lists;//存储webpage_info的list
    public DownloadServices.DownloadBinder mDownloadBinder;
    public DownloadInfo downloadInfo;//下载信息
    private SomeTools someTools;

    private FrameLayout f1;//放置webview的容器
    private TextView mTextView;//主界面的工具栏里的搜索框
    private View matcheTextView;//搜索webview文字的搜索框
    private MultPage_Dialog md;//多窗口dialogFragment
    private HandleClickedLinks handleClickedLinks;
    private ControlWebView controlInterface;
    private static boolean seviceBund = false;//绑定服务时把它改为true；
    private View mSearchToolView;//搜索界面，包括有历史匹配和快捷输入
    private boolean isOpenedEdit = false;//是否打开了搜索栏上的搜索框
    private ImageView menuButton;//底部菜单按钮
    private ImageView multButton;//多窗口按钮
    private ProcessUrl mProcessUrl;//处理字符串的类
    private static boolean isVertical;//当前屏幕是竖直状态还是横屏状态，竖直是true
    private WebViewChromeClientInterface chromeClientInterface;//上传文件接口
    private ProgressBar bar;//网页加载进度条

    //权限
    String[] allperm = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
    String[] locatePerm = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private ValueCallback<Uri[]> fileUploadCallBack;
    private OpenWebview mOpenWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firstInstall();//判断是不是第一次安装
        setContentView(R.layout.activity_main);

        //实现处理webview长按事件的接口
        achieveHandlerClickInterface();
        //实现控制webview的接口
        implControlexplorer();
        //获取WebViewManager的实例
        mWebViewManager = WebViewManager.newInstance(MainActivity.this, handleClickedLinks);

        //获取Converted_Webpage_List,并传入mWebViewManager注册观察者
        mConverted_lists = WebViewInfo_Manager.get(mWebViewManager);

        //初始化Sometools
        someTools = SomeTools.INSTANCES;
        someTools.SetContext(this);

        //实例化某些view
        f1 = findViewById(R.id.Webview_group);
        mTextView = findViewById(R.id.search_edittext);

        //判断webviewmanager中有没有webview，有的话执行恢复方法，把webview重新放进屏幕。当新进应用，是没有webview的，那么添加wevbview，否则，就把activity  stop()时remove的view加载回来
        if (mWebViewManager.isempty()) {
            LogUtil.d(TAG, "onCreate: isempty");
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

        if (savedInstanceState != null) {
            //取回保存的数据
            mTextView.setText(savedInstanceState.getString(WEBTITLE));
            current = savedInstanceState.getInt(CURRENTINT);
            //isOpenedEdit = savedInstanceState.getBoolean(OPENED_EDIT);
        }
        LogUtil.d("lifecycle", "onCreate()");

//接口回调
        openwebpage_fromhistoryORbookmark();
        useMultPage_DialogFragmentInterface();
        downloadDialog_startDownload();
        implchromeClientInterface();//实现文件上传，实现网页调用打开新窗口的方法
        //implOpenWendow();//实现网页调用打开新窗口的方法

        multButton = findViewById(R.id.mult_button);
        menuButton = findViewById(R.id.menu_button);
    }

    @Override
    protected void initActivity(BaseLifecycleObserver observer) {
        observer = new MainLifeCycleObserver(this);
        getLifecycle().addObserver(observer);

        LogUtil.d(TAG, "测试initActivity，重写oncreate后此方法被调用了");
    }

    /**
     * 测试上下文
     */
    private void testContext() {
        LogUtil.d(TAG, "测试上下文，getApplication得到的是:  " + getApplicationContext());
        LogUtil.d(TAG, "测试上下文，Xapplication.getInstance()得到的是：   " + Xapplication.getInstance());
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
        LogUtil.d("lifecycle", "onReStart()");
    }


    @Override
    protected void onStart() {
        super.onStart();
//旋转屏幕会重建，所以获取横竖屏状态，因为横屏时使用自己写的下载器时，dialogfragment会报错，所以横屏时使用系统内置下载器
        isVertical = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        //获取搜索引擎
        if (null == mProcessUrl) {
            mProcessUrl = new ProcessUrl();
        }
        LogUtil.d("lifecycle", "onStart'");
        EventBus.getDefault().register(this);
        bar = findViewById(R.id.webviewProgressBar);
        mWebViewManager.setOnUpdateProgress(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
       /* int s = mWebViewManager.size();
        LogUtil.d("lifecycle", "onResume()" + "webview数量" + s);*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d("lifecycle", "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isOpenedEdit)//如果没打开搜索，就暂停视图，否则，就不停止当前的webview，如此，可以获得搜索建议
            mWebViewManager.getTop(current).onPause();
        //f1.removeAllViews();//移除所有视图
        EventBus.getDefault().unregister(this);
        LogUtil.d("lifecycle", "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (seviceBund)
            unbindService(connection);
        LogUtil.d("lifecycle", "onDestroy()");
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(WEBTITLE, mTextView.getText().toString());
        outState.putInt(CURRENTINT, current);
        outState.putBoolean(OPENED_EDIT, isOpenedEdit);
    }

    /**
     * 旋转屏幕会重建，所以获取横竖屏状态，因为横屏时使用自己写的下载器时，dialogfragment会报错，所以横屏时使用系统内置下载器
     *
     * @return true时是竖屏，false是横屏
     */
    public static boolean IsVertical() {
        return isVertical;
    }

    /**
     * @param progress 网页加载进度
     *                 更新进度条进度
     *                 如果进度是-1或是100时，隐藏进度条
     */
    @Override
    public void update(int progress) {
        if (bar.getVisibility() == View.GONE) {
            bar.setProgress(0);
            bar.setVisibility(View.VISIBLE);
        }

        if (progress == -1 || progress == 100) {
            bar.setVisibility(View.GONE);
        } else
            bar.setProgress(progress);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Check if the key event was the Back button and if there's history
        //这里还要处理其他的返回事件,当返回true，事件就不再向下传递，也就是处理完这个事件就让别的再处理
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isOpenedSearchText) {
                //先处理webview的文本搜索
                closeMatchesText();
            } else if (mWebViewManager.getTop(current).canGoBack()) {//处理在没有文本搜索的时候
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
        String home_url = mWebViewManager.procressUrl(url);//处理网址

        //由多窗口的新建主页按钮调用，作用是新建webview放进mclist的最后的位置，remove掉旧的webivew视图，刷新视图。
        if (!mWebViewManager.isempty()) {
            mWebViewManager.stop(current);
            f1.removeView(mWebViewManager.getTop(current));
            current = mWebViewManager.size();
        }

        mWebViewManager.newWebView(current, getApplicationContext(), MainActivity.this, home_url);
        mWebViewManager.getTop(current).setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String title, String selectText) {
                Toast.makeText(MainActivity.this, title + selectText, Toast.LENGTH_LONG).show();
            }
        });

        f1.addView(mWebViewManager.getTop(current));
        //新建标签页默认插入最后的位置。
    }

    /**
     * @param url 网址
     *            使用菜单，后台打开网页使用此方法
     */
    public void newTabInBackground(String url) {
        mWebViewManager.newWebView(current + 1, getApplicationContext(), MainActivity.this, url);
        mWebViewManager.getTop(current + 1).setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String title, String selectText) {
                Toast.makeText(MainActivity.this, title + selectText, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void delete_page(int position) {
        deleteWebview(position);
    }

    public void deleteWebview(int position) {
        if (mWebViewManager.size() == 1) {
            newTab(null);//newTab，先建一个标签页，然后删除旧有的，newtab将会使得current+=1，所以这里要减1
            mWebViewManager.removeToTrash(position, false);
            current -= 1;
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

    /**
     * @param pos 是指要切换到的页面
     */
    @Override
    public void switchPage(int pos) {
        WebView old = mWebViewManager.getTop1(current);
        WebView willSwitch = mWebViewManager.getTop1(pos);

        old.onPause();
        f1.removeView(old);
        f1.addView(willSwitch);
        current = pos;
        willSwitch.onResume();

        LogUtil.d(TAG, "switchPage: " + willSwitch.getProgress());
        setTextForbar(current);//更新工具栏上的文字
        update(willSwitch.getProgress());
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
        if (mOpenWebview == null) {
            mOpenWebview = new OpenWebview();
        }
        BookmarkPageActivity.setInterface(mOpenWebview);
        HistorysActivity.setInterface(mOpenWebview);

    }

    /**
     * flags为真，新建标签页打开；
     * flags为假，在当前页面打开；
     * 为在历史记录或是收藏记录中，点击打开时被调用
     */
    class OpenWebview implements OpenOneWebpage {

        @Override
        public void loadUrl(String url, boolean flags) {
            if (flags) {
                newTab(url);
            } else {
                mWebViewManager.getTop(current).loadUrl(url);
            }
        }
    }

    public void useMultPage_DialogFragmentInterface() {
        MultPage_Dialog.setInterface(this);
    }

    /**
     * @param pos 当前webview在列表中的位置
     *            <p>
     *            工具栏的的文字更新，获取当前浏览网页的标题，设置到底栏
     */
    void setTextForbar(int pos) {
        //String mt = mConverted_lists.getUrl(pos);
        String mt = mWebViewManager.getTop1(pos).getUrl();
        if (mt.equals(SomeRes.default_homePage_url)) {
            //不显示默认主页的url
            mt = "";
        }
        mTextView.setText(mt);
    }

    /**
     * 展示多窗口
     */
    public void mult_dialog(View v) {
        FragmentManager fm = getSupportFragmentManager();
        md = new MultPage_Dialog();
        md.show(fm, "fragment_multPage_dialog");
    }

    /**
     * 底部菜单界面
     */
    public void buttomMenu(View v) {
        FragmentManager fm = getSupportFragmentManager();
        MinSetDialog md = MinSetDialog.newInstance(mConverted_lists.getInfo(current));
        md.setInterafce(controlInterface);//控制网页的一些方法交给MinSetDialog
        md.show(fm, "minSetDialog");
    }

    /**
     * 搜索框代码,由activity_main.xml中的搜索框调用,展示搜索框,输入文本，载入网页
     */
    public void searchBar(View v) {
        isOpenedEdit = true;
        Intent intent = new Intent(MainActivity.this, ContentToUrlActivity.class);
        intent.putExtra("text_or_url", mTextView.getText().toString());
        //把当前网页网址传进去
        startActivityForResult(intent, 21);
    }

    /**
     * 把webview的页内文本搜索收起来
     */
    private void closeMatchesText() {
        matcheTextView.setVisibility(View.INVISIBLE);
        isOpenedSearchText = false;
        mWebViewManager.clearMatches(current);
    }

    /**
     * 打开页内文本搜索功能
     */
    public void openMatchesTextView() {

        ViewStub stub = findViewById(R.id.viewStub_search);
        if (stub != null) {
            //stubView只inflate一次，所以，inflate之后会变成null，再次inflate会报错
            matcheTextView = stub.inflate();
        }
        matcheTextView.setVisibility(View.VISIBLE);
        isOpenedSearchText = true;

        EditText editText = matcheTextView.findViewById(R.id.textView_search);
        ImageButton goaHead = matcheTextView.findViewById(R.id.goahead);
        ImageButton back = matcheTextView.findViewById(R.id.back);

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
            LogUtil.d(TAG, "onActivityResult: 结果失败");
            if (requestCode == 2020) {
                cancelUpLoad();//未选择任何文件，消费掉callback。
            }
            return;
        }
        if (requestCode == 21) {
            //把DoSearchActivity的requestCode定义为21
            assert data != null;
            mWebViewManager.getTop(current).loadUrl(data.getStringExtra("text_or_url"));
            mTextView.setText("正在载入...");
            isOpenedEdit=false;
            LogUtil.d(TAG, "onActivityResult: 被触发" + data.getStringExtra("text_or_url"));
        }
        if (requestCode == 2020) {
            if (fileUploadCallBack == null) {
                return;
            }
            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
            if (result != null) {
                fileUploadCallBack.onReceiveValue(new Uri[]{result});
            } else {
                LogUtil.d(TAG, "onActivityResult: 选择文件结果，result是null");
                fileUploadCallBack.onReceiveValue(new Uri[]{});
            }
            fileUploadCallBack = null;
            //fileUploadCallBack.onReceiveValue(CustomWebchromeClient.FileChooserParams.parseResult(2020,data));
            LogUtil.d(TAG, "onActivityResult: " + data.getData().toString());
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
            handleClickedLinks = new HandleClickLinksImpl();
        }
    }


    class HandleClickLinksImpl implements HandleClickedLinks {
        @Override
        public void onImgSelected(int x, int y, int type, String extra) {
            LogUtil.d(TAG, "onImgSelected: " + extra);
            String[] menu = new String[]{"复制图片链接地址", "新窗口打开图片", "分享图片", "保存图片"};
            new AlertDialog.Builder(MainActivity.this).setItems(menu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            //复制链接地址
                            Toast.makeText(MainActivity.this, "点了链接：" + extra, Toast.LENGTH_LONG).show();
                            //clipData(extra);
                            someTools.clipData(extra);
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
                            someTools.savePic(extra);
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
            LogUtil.d(TAG, "onLinkSelected: " + extra);
            String[] menu = new String[]{"复制链接地址", "新窗口打开", "分享", "后台打开", "添加到书签"};
            new AlertDialog.Builder(MainActivity.this).setItems(menu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            //复制链接地址
                            Toast.makeText(MainActivity.this, "复制了链接：" + extra, Toast.LENGTH_LONG).show();
                            //clipData(extra);
                            someTools.clipData(extra);
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

    }


    /**
     * 实现一些控制网页的方法，用于minsetDialog或者长按菜单
     */
    private void implControlexplorer() {
        if (controlInterface == null) {
            controlInterface = new ControlWebViewImpl();
        }

    }

    class ControlWebViewImpl implements com.example.kiylx.ti.interfaces.ControlWebView {
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
            openMatchesTextView();
        }

        @Override
        public void usePcMode() {
            mWebViewManager.reLoad_pcmode();
        }

        @Override
        public void newPage(String url) {
            newTab(url);
        }

        @Override
        public void saveWeb() {
            WebView tmp = mWebViewManager.getTop(current);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), tmp.getTitle() + TimeProcess.getTime() + ".mht");
            tmp.saveWebArchive(file.getAbsolutePath());
        }

        @Override
        public void printPdf() {
            WebView tmp = mWebViewManager.getTop(current);
            someTools.printPdf(tmp);
        }
    }

    /**
     * 上传文件方法,打开新窗口方法，皆在此实现，并传入webviewmanager
     */
    private void implchromeClientInterface() {
        if (chromeClientInterface == null) {
            chromeClientInterface = new ChromeClientInterfaceImpl();
        }
        mWebViewManager.setOnClientInterface(chromeClientInterface);

    }

    /**
     * 文件上传，打开新窗口等的接口的实现
     */
    class ChromeClientInterfaceImpl implements WebViewChromeClientInterface {
        //false则是自己写的方法，能上传所有文件，不去理会是什么type，默认值是true，以系统提供的createIntent方式获取文件。
        boolean defUploadMode = DefaultPreferenceTool.getBoolean(getApplicationContext(), getString(R.string.uploadMode_key), true);

        @Override
        public boolean upload(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            cancelUpLoad();//清理一次请求，防止上一次的没有消费掉而出错
            fileUploadCallBack = filePathCallback;
            try {
                if (!defUploadMode) {
                    //选择文件
                    Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                    intent2.addCategory(Intent.CATEGORY_OPENABLE);
                    intent2.setType("*/*");
                    startActivityForResult(Intent.createChooser(intent2, "上传文件"), 2020);
                } else {
                    Intent intent = fileChooserParams.createIntent();
                    LogUtil.d(TAG, "onShowFileChooser: type:" + intent.getType() + " action:" + intent.getAction() + " category:" + intent.getCategories());
                    startActivityForResult(Intent.createChooser(intent, "上传文件"), 2020);
                }

            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                return true;
            }
            return true;


        }

        /**
         * webview对于
         * <a href="http://www.google.com" target="_blank">new window</a>
         * 这种形式的tag，会要求起一个新窗口打开，而要截获这个请求，则可以在webChromeClient的onCreateWindow()回调函数中进行处理，
         */
        @Override
        public WebView OpenWindow(Context url) {
            mWebViewManager.stop(current);
            f1.removeView(mWebViewManager.getTop(current));
            CustomAWebView tmp = mWebViewManager.newWebview(++current, url, MainActivity.this);
            f1.addView(mWebViewManager.getTop(current));
            return tmp;
        }

        /**
         * @param origin   请求地理位置的网址
         * @param callback 处理请求的回调接口，调用invoke方法处理是否给于origin地理位置
         */
        @Override
        public void requestLocate(String origin, GeolocationPermissions.Callback callback) {
            //如果有存储权限，则可以开始下载，否则告诉用户申请权限
            if (!EasyPermissions.hasPermissions(MainActivity.this, locatePerm)) {
                EasyPermissions.requestPermissions(MainActivity.this, "没有地理位置权限，请去设置给予权限后再试", 20033, locatePerm);
                callback.invoke(origin, false, false);
                return;
            }
            //GeolocationPermissions.getInstance().allow(origin);
            new AlertDialog.Builder(MainActivity.this).setMessage("当前网址想要使用你的地理位置")
                    .setPositiveButton("允许", (dialog, which) -> {
                        callback.invoke(origin, true, false);
                        LogUtil.d(TAG, "requestLocate: 位置请求成功");
                    })
                    .setNegativeButton("拒绝", (dialog, which) -> {
                        callback.invoke(origin, false, false);
                        LogUtil.d(TAG, "requestLocate: 位置请求失败");
                    })
                    .setCancelable(false)
                    .show();
        }

    }

    /**
     * ValueCallback<Uri[]> filePathCallback这个东西，在没有清理的前提下再次执行会出错。
     * 所以这里清理掉上次的残留
     */
    private void cancelUpLoad() {

        if (fileUploadCallBack != null) {
            fileUploadCallBack.onReceiveValue(null);
            fileUploadCallBack = null;
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