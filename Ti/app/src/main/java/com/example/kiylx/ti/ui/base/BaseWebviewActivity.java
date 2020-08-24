package com.example.kiylx.ti.ui.base;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.GeolocationPermissions;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.conf.ActivityCode;
import com.example.kiylx.ti.conf.StateManager;
import com.example.kiylx.ti.downloadpack.DownloadActivity;
import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;
import com.example.kiylx.ti.downloadpack.dinterface.DownloadInterfaceImpl;
import com.example.kiylx.ti.downloadpack.downloadcore.DownloadServices;
import com.example.kiylx.ti.downloadpack.fragments.DownloadDialog;
import com.example.kiylx.ti.interfaces.ActionSelectListener;
import com.example.kiylx.ti.interfaces.ControlWebView;
import com.example.kiylx.ti.interfaces.HandleClickedLinks;
import com.example.kiylx.ti.interfaces.MultiDialog_Functions;
import com.example.kiylx.ti.interfaces.OpenOneWebpage;
import com.example.kiylx.ti.interfaces.WebViewChromeClientInterface;
import com.example.kiylx.ti.model.EventMessage;
import com.example.kiylx.ti.mvp.contract.MainActivityContract;
import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.presenter.WebViewInfo_Manager;
import com.example.kiylx.ti.mvp.presenter.WebViewManager;
import com.example.kiylx.ti.mvp.presenter.lifecycles.MainLifeCycleObserver;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.tool.ProcessUrl;
import com.example.kiylx.ti.tool.SomeTools;
import com.example.kiylx.ti.tool.dateProcess.TimeProcess;
import com.example.kiylx.ti.tool.preferences.DefaultPreferenceTool;
import com.example.kiylx.ti.tool.preferences.PreferenceTools;
import com.example.kiylx.ti.trash.BookmarkPageActivity;
import com.example.kiylx.ti.ui.activitys.HistorysActivity;
import com.example.kiylx.ti.ui.activitys.StartPageActivity;
import com.example.kiylx.ti.ui.fragments.dialogfragment.MultPage_Dialog;
import com.example.kiylx.ti.webview32.CustomAWebView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/23 9:31
 * packageName：com.example.kiylx.ti.ui.base
 * 描述：此基类提供对于webview的管理和控制，对于加入书签，更新进度条，页内搜索等等的功能提供支持但不提供具体实现。
 * 使用这个基类，必须保证xml布局中有一个framelayout作为webview的存放容器，且id要被命名为Webview_group
 */
public class BaseWebviewActivity extends BaseActivity implements MultiDialog_Functions, WebViewManager.UpdateProgress, MainActivityContract {
    private static final String TAG = "MainActivity";
    private static final String OPENED_EDIT = "opened_edit";

    public static int current = 0;//静态变量，保存current的值，防止activity被摧毁时重置为0；
    private String WEBTITLE = "webTitle";//用来存放当前标签页的网址，在旋转屏幕时能恢复下方搜索栏上的文字
    private String CURRENTINT = "currentInt";//用来存放指示当前webview的位置

    protected WebViewManager mWebViewManager;
    private WebViewInfo_Manager mConverted_lists;//存储webpage_info的list
    public DownloadServices.DownloadBinder mDownloadBinder;
    public DownloadInfo downloadInfo;//下载信息
    private SomeTools someTools;

    private FrameLayout f1;//放置webview的容器
    private HandleClickedLinks handleClickedLinks;
    private static boolean seviceBund = false;//绑定服务时把它改为true；
    protected boolean isOpenedEdit = false;//是否打开了搜索栏上的搜索框
    private ProcessUrl mProcessUrl;//处理字符串的类
    private static boolean isVertical;//当前屏幕是竖直状态还是横屏状态，竖直是true
    private WebViewChromeClientInterface chromeClientInterface;//上传文件接口

    //权限
    String[] allperm = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
    String[] locatePerm = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private ValueCallback<Uri[]> fileUploadCallBack;
    private OpenWebview mOpenWebview;
    protected StateManager stateManager;


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        //查找到存放webview组件的viewgroup
        f1 = findViewById(R.id.Webview_group);
    }

    @Override
    protected void initActivity(BaseLifecycleObserver observer, Bundle savedInstanceState) { //实现处理webview长按事件的接口
        achieveHandlerClickInterface();
        //获取WebViewManager的实例
        mWebViewManager = WebViewManager.newInstance(BaseWebviewActivity.this, handleClickedLinks);
        stateManager=SomeTools.getXapplication().getStateManager();

        //获取Converted_Webpage_List,并传入mWebViewManager注册观察者
        mConverted_lists = WebViewInfo_Manager.get(mWebViewManager);

        //初始化Sometools
        someTools = SomeTools.INSTANCES;
        someTools.SetContext(this);

        //判断webviewmanager中有没有webview，有的话执行恢复方法，把webview重新放进屏幕。当新进应用，是没有webview的，那么添加wevbview，否则，就把activity  stop()时remove的view加载回来
        if (mWebViewManager.isempty()) {
            LogUtil.d(TAG, "onCreate: isempty");
            newTab(null);
        } else {
            mWebViewManager.getTop(current).onResume();

            //注：获取当前webview的父视图，然后再把这个webview从父视图中移除，然后再重新添加，解决白屏问题。
            WebView webView = mWebViewManager.getTop(current);
            if (webView.getParent() != null) {
                ((ViewGroup) webView.getParent()).removeView(webView);
            }
            f1.addView(webView);
        }

        if (savedInstanceState != null) {
            //取回保存的数据
            current = savedInstanceState.getInt(CURRENTINT);
            //isOpenedEdit = savedInstanceState.getBoolean(OPENED_EDIT);
        }
        LogUtil.d("lifecycle", "onCreate()");

//接口回调
        openwebpage_fromhistoryORbookmark();
        downloadDialog_startDownload();
        implchromeClientInterface();//实现文件上传，实现网页调用打开新窗口的方法
        useMultPage_DialogFragmentInterface();

        observer = new MainLifeCycleObserver(this);
        getLifecycle().addObserver(observer);

        LogUtil.d(TAG, "测试initActivity，重写oncreate后此方法被调用了");

    }

    @Override
    protected void initViewAfter(Bundle savedInstanceState) {
        super.initViewAfter(savedInstanceState);
    }

    @Override
    protected int layoutId() {
        return 0;
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
                provideCurrentWebviewTitle(current);
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
        mWebViewManager.setOnUpdateProgress(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //如果没打开搜索，就暂停视图，否则，就不停止当前的webview，如此，可以获得搜索建议
        if (!isOpenedEdit) mWebViewManager.getTop(current).onPause();
        EventBus.getDefault().unregister(this);
        mWebViewManager.setOnUpdateProgress(null);
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
     * @return 获取当前webview在arraylist中的位置
     */
    public static int getCurrent() {
        return current;
    }


    /**
     * 第一次安装后启动，写入一个preference，判断是不是第一次打开
     * 若是，打开启动页，并关闭mainactivity，在startpageactivity中作出一系列的初始化操作
     */
    private void firstInstall() {

        if (!PreferenceTools.getBoolean(this, "Installed")) {
            //如果是第一次打开应用Installed不存在，默认拿到false。则可以在这里做一些初始化操作。之后在StartPageActivity中写入Installed为true。
            Intent intent = new Intent(BaseWebviewActivity.this, StartPageActivity.class);
            startActivity(intent);//打开启动页activity
            finish();//结束mainactivity
        }

    }

    /**
     * 退出应用或是其他原因使webview从屏幕上被移除，那就用这个方法恢复
     */
    private void reasumeWebview() {
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

        mWebViewManager.newWebView(current, getApplicationContext(), BaseWebviewActivity.this, home_url);
        mWebViewManager.getTop(current).setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String title, String selectText) {
                Toast.makeText(BaseWebviewActivity.this, title + selectText, Toast.LENGTH_LONG).show();
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
        mWebViewManager.newWebView(current + 1, getApplicationContext(), BaseWebviewActivity.this, url);
        mWebViewManager.getTop(current + 1).setActionSelectListener(new ActionSelectListener() {
            @Override
            public void onClick(String title, String selectText) {
                Toast.makeText(BaseWebviewActivity.this, title + selectText, Toast.LENGTH_LONG).show();
            }
        });
    }

    //=====================================UpdateProgress接口======================
    /**
     * @param progress 网页加载进度
     *                 网页加载时会调用此方法，因此，可以重写此方法以显示加载进度
     *                 如果进度是-1或是100时，隐藏进度条
     */
    @Override
    public void updateWebviewProgress(int progress) {

    }

    //=====================================MultiDialog_Functions接口=============
    @Override
    public void click_newPagebutton() {
        //新建标签页
        newTab(null);
    }
    @Override
    public void delete_page(int position) {
        deleteWebview(position);
    }

    /**
     * @param position webview在list中的位置
     *                 删除指定位置上的webview，同时更新界面
     */
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
        provideCurrentWebviewTitle(current);//更新标题栏
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
        provideCurrentWebviewTitle(current);//更新工具栏上的文字
        updateWebviewProgress(willSwitch.getProgress());
    }

   //===================================OpenOneWebpage接口=======================

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
    //=========================页内搜索、更新标题栏，多窗口、搜索==========================

    /**
     * @param pos 当前webview在列表中的位置
     *            <p>
     *            工具栏的的文字更新，获取当前浏览网页的标题，设置到底栏
     */
    protected void provideCurrentWebviewTitle(int pos) {
    }

    /**
     * 展示多窗口
     * xml布局中直接调用此方法展示多重口界面
     */
    @CallSuper
    public void mult_dialog(View v) {
    }

    /**
     * 搜索框代码,
     * 使用搜索功能时，xml中可直接调用,展示搜索框,输入文本然后就可以载入网页
     */
    @CallSuper
    protected void openSearchView(View v) {
        isOpenedEdit = true;
    }

    /**
     * 把webview的页内文本搜索收起来
     */

    @CallSuper
    protected void closeMatchesText() {
        mWebViewManager.clearMatches(current);
    }

    /**
     * 页内搜索内容时寻找下一个匹配项
     */
    protected void SearchTextGoHead(){
        mWebViewManager.goaHead(current);
    }

    /**
     * 页内搜索内容时寻找上一个匹配项
     */
    protected void SearchTexBback(){
        mWebViewManager.text_back(current);
    }

    /**
     * @param beSearched 被搜索的文字
     *                   页内搜索时，调用此方法匹配高亮所有项
     */
    protected void findAllText(String beSearched){
        mWebViewManager.findAllAsync(current, beSearched);
    }
    //==========================onActivityResult==============================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            LogUtil.d(TAG, "onActivityResult: 结果失败");
            if (requestCode == ActivityCode.uploadFile) {
                cancelUpLoad();//未选择任何文件，消费掉callback。
            }
            return;
        }
        if (requestCode == ActivityCode.uploadFile) {
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

//================================下载==============================
    /**
     * 检查下载所需的权限，并实现下载接口，以及开启下载服务
     * 让开始下载任务的dialog调用这个方法来开启并绑定服务，开始下载。
     */
    private void downloadDialog_startDownload() {
        DownloadDialog.setMinterface(new DownloadInterfaceImpl() {

            @Override
            public void startDownoadService(DownloadInfo info) {

                //如果有存储权限，则可以开始下载，否则告诉用户申请权限
                if (EasyPermissions.hasPermissions(BaseWebviewActivity.this, allperm)) {
                    //Toast.makeText(MainActivity.this,"已赋予权限",Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(BaseWebviewActivity.this, DownloadServices.class);
                    startService(intent);
                    bindService(intent, connection, BIND_AUTO_CREATE);
                    seviceBund = true;
                    downloadInfo = info;
                } else {
                    EasyPermissions.requestPermissions(BaseWebviewActivity.this, "没有文件读写权限，请去设置给予权限后再试", 20033, allperm);
                }


            }
        });
    }

    /**
     * 打开下载界面
     */
    protected void openDownloadView() {
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
//===============================webview内长按回调===================================
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
            new AlertDialog.Builder(BaseWebviewActivity.this).setItems(menu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            //复制链接地址
                            Toast.makeText(BaseWebviewActivity.this, "点了链接：" + extra, Toast.LENGTH_LONG).show();
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
            new AlertDialog.Builder(BaseWebviewActivity.this).setItems(menu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            //复制链接地址
                            Toast.makeText(BaseWebviewActivity.this, "复制了链接：" + extra, Toast.LENGTH_LONG).show();
                            //clipData(extra);
                            someTools.clipData(extra);
                            break;
                        case 1:
                            //新窗口打开
                            newTab(extra);
                            break;
                        case 2:
                            //分享
                            sharing(extra);
                            break;
                        case 3:
                            //后台打开
                            newTabInBackground(extra);
                            break;
                        case 4:
                            //添加到书签
                            addtobookmark(extra);
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

    @Override
    public void sharing(String url) {
        share(url);
    }

    /**
     * @param url 网址
     *            把信息添加到书签记录
     */
    @Override
    public void addtobookmark(String url) {
    }

    /**
     * 重载网页
     */
    @Override
    public void reload() {
        mWebViewManager.reLoad((BaseWebviewActivity.this));
    }

    /**
     * 页内搜索
     */
    @Override
    public void openPageInsideSearchView() {
    }

    @Override
    public void usePcMode() {
        mWebViewManager.reLoad_pcmode();
    }

    @Override
    public void newPage(String url) {
        newTab(url);
    }
//=====================================保存网页、打印、上传、地理位置============================
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

    public void useMultPage_DialogFragmentInterface() {
        MultPage_Dialog.setInterface(this);
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
            CustomAWebView tmp = mWebViewManager.newWebview(++current, url, BaseWebviewActivity.this);
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
            if (!EasyPermissions.hasPermissions(BaseWebviewActivity.this, locatePerm)) {
                EasyPermissions.requestPermissions(BaseWebviewActivity.this, "没有地理位置权限，请去设置给予权限后再试", 20033, locatePerm);
                callback.invoke(origin, false, false);
                return;
            }
            //GeolocationPermissions.getInstance().allow(origin);
            new AlertDialog.Builder(BaseWebviewActivity.this).setMessage("当前网址想要使用你的地理位置")
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
