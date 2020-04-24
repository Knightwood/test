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
import androidx.appcompat.app.AppCompatActivity;

import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.crystal.customview.Slider.Slider;
import com.example.kiylx.ti.managercore.CustomAWebView;
import com.example.kiylx.ti.myInterface.FileUpload;
import com.example.kiylx.ti.myInterface.OpenWindowInterface;
import com.example.kiylx.ti.tool.SavePNG_copyText;
import com.example.kiylx.ti.tool.PreferenceTools;
import com.example.kiylx.ti.conf.WebviewConf;
import com.example.kiylx.ti.managercore.WebViewInfo_Manager;
import com.example.kiylx.ti.downloadPack.base.DownloadInfo;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.downloadPack.downloadCore.DownloadServices;
import com.example.kiylx.ti.downloadPack.fragments.DownloadDialog;
import com.example.kiylx.ti.tool.EventMessage;
import com.example.kiylx.ti.tool.dateProcess.TimeProcess;
import com.example.kiylx.ti.ui.fragments.Bookmark_Dialog;
import com.example.kiylx.ti.ui.fragments.MultPage_Dialog;
import com.example.kiylx.ti.myInterface.ActionSelectListener;
import com.example.kiylx.ti.myInterface.ControlWebView;
import com.example.kiylx.ti.downloadPack.downInterface.DownloadInterfaceImpl;
import com.example.kiylx.ti.myInterface.HandleClickedLinks;
import com.example.kiylx.ti.myInterface.MultiDialog_Functions;
import com.example.kiylx.ti.myInterface.OpenOneWebpage;
import com.example.kiylx.ti.managercore.WebViewManager;
import com.example.kiylx.ti.ui.fragments.MinSetDialog;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.tool.ProcessUrl;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements MultiDialog_Functions {
    private static final String TAG = "MainActivity";
    private static final String CURRENT_URL = "current url";
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

    private FrameLayout f1;//放置webview的容器
    private EditText mTextView;//主界面的工具栏里的搜索框
    //private ActivityMainBinding mainBinding;//用于更新搜索框标题的databinding
    private View matcheTextView;//搜索webview文字的搜索框
    private MultPage_Dialog md;//多窗口dialogFragment
    private HandleClickedLinks handleClickedLinks;
    private ControlWebView controlInterface;
    private static boolean seviceBund = false;//绑定服务时把它改为true；
    //private boolean isHide;//控制toolbar上菜单的显示与隐藏
    private View mSearchToolView;//搜索界面，包括有历史匹配和快捷输入
    private boolean isOpenedEdit = false;//是否打开了搜索栏上的搜索框
    private ImageView menuButton;//底部菜单按钮
    private ImageView multButton;//多窗口按钮
    private ProcessUrl mProcessUrl;//处理字符串的类
    private static boolean isVertical;//当前屏幕是竖直状态还是横屏状态，竖直是true
    private FileUpload iupload;//上传文件接口
    private boolean useNewSearchStyle = true;

    //权限
    String[] allperm = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
    private ValueCallback<Uri[]> fileUploadCallBack;
    private OpenWindowInterface mOpenWindowInterface;//webchromeclient打开新窗口时用的接口


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

        if (savedInstanceState != null) {
            //取回保存的数据
            mTextView.setText(savedInstanceState.getString(WEBTITLE));
            current = savedInstanceState.getInt(CURRENTINT);
            //isOpenedEdit = savedInstanceState.getBoolean(OPENED_EDIT);
        }
        Log.d("lifecycle", "onCreate()");

//接口回调
        openwebpage_fromhistoryORbookmark();
        useMultPage_DialogFragmentInterface();
        downloadDialog_startDownload();
        implFileUpload();//实现文件上传
        implOpenWendow();//实现网页调用打开新窗口的方法

        useNewSearchStyle = PreferenceTools.getBoolean(this, SomeRes.SearchViewStyle, true);

        multButton = findViewById(R.id.mult_button);
        menuButton = findViewById(R.id.menu_button);


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
//旋转屏幕会重建，所以获取横竖屏状态，因为横屏时使用自己写的下载器时，dialogfragment会报错，所以横屏时使用系统内置下载器
        isVertical = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        //获取搜索引擎
        if (null == mProcessUrl) {
            mProcessUrl = new ProcessUrl();
        }
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
        if (isOpenedEdit)
            closeSearchEdit();
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // Check if the key event was the Back button and if there's history
        //这里还要处理其他的返回事件,当返回true，事件就不再向下传递，也就是处理完这个事件就让别的再处理
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isOpenedEdit) {
                closeSearchEdit();
                return true;
            }
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

    /**
     * 展示多窗口
     */
    public void mult_dialog(View v) {

        FragmentManager fm = getSupportFragmentManager();
        md = new MultPage_Dialog();
        md.show(fm, "fragment_multPage_dialog");
    }

    /**
     * 底部设置界面
     */
    public void buttomMenu(View v) {

        FragmentManager fm = getSupportFragmentManager();
        MinSetDialog md = MinSetDialog.newInstance(mConverted_lists.getInfo(current));
        md.setInterafce(controlInterface);//控制网页的一些方法交给MinSetDialog
        md.show(fm, "minSetDialog");
    }

    /**
     * 搜索框代码,由activity_main.xml中的搜索框调用
     * false是旧样式
     * true是新样式
     */
    public void searchBar(View v) {
        if (useNewSearchStyle) {
            openSearchEdit();
        } else {
            search_dialog();
        }

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
     * 打开搜索框
     */
    private void openSearchEdit() {
        //mTextView 搜索框
        //获取焦点并弹出键盘
        mTextView.setFocusableInTouchMode(true);
        mTextView.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(mTextView, 0);
        mTextView.selectAll();
        ViewStub stub = findViewById(R.id.viewStub_search_tool);
        if (stub != null) {
            mSearchToolView = stub.inflate();
        }

        openAnim();
        isOpenedEdit = true;

        Slider slider = mSearchToolView.findViewById(R.id.slidrp);
        slider.setOnPlay(new Slider.Play() {
            //接收滑动杆的移动事件，通过Str接口转发。
            @Override
            public void send(Slider.shiftPos shift) {
                Log.d(TAG, "滑动事件: " + shift.toString());
                shiftStr(shift.toString());
            }

        });


        mTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    Log.d(TAG, "onKeyDown: ");
                    //监听回车键，按下的时候就开始执行搜索操作。
                    String s = mTextView.getText().toString();
                    if (!s.equals("")) {
                        Log.d(TAG, "onKey: " + s + ":" + ProcessUrl.processString(s, getApplicationContext()));
                        mWebViewManager.getTop(current).loadUrl(ProcessUrl.processString(s, getApplicationContext()));
                    }
                    closeSearchEdit();
                    return true;
                }
                return false;
            }
        });
        //透明的view，点击后关掉这个界面
        ImageView closeView = findViewById(R.id.image_alpha0);
        closeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearchEdit();
            }
        });
    }

    /**
     * @param s left,right,stop，向左移动，向右移动，或是停止
     *          移动光标
     */
    public void shiftStr(String s) {
        if (!(s).equals("STOP")) {
            //只要不是STOP，获取输入框字符串长度，获取当前光标位置，然后移动光标
            int endPos = mTextView.length();
            int now = mTextView.getSelectionStart();
            if (s.equals("LEFT") && now > 0) {
                mTextView.setSelection(--now);
            } else {
                if (now < endPos)
                    mTextView.setSelection(++now);
            }


        }
    }

    //以下三个方法，把文字追加到搜索框
    public void writewww(View v) {
        mTextView.append("www.");
    }

    public void writehttp(View v) {
        mTextView.append("http://");
    }

    public void writecom(View v) {
        mTextView.append(".com");
    }

    private void closeIME(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    //关闭搜索框
    private void closeSearchEdit() {
        mTextView.clearFocus();
        mTextView.setFocusableInTouchMode(false);
        isOpenedEdit = false;
        closeAnim();
        closeIME(mTextView);
    }

    //打开搜索框的动画
    private void openAnim() {
        mSearchToolView.setAlpha(0f);
        mSearchToolView.setVisibility(View.VISIBLE);
        mSearchToolView.animate().alpha(1f).setDuration(500L).start();
        multButton.setVisibility(View.GONE);
        menuButton.setVisibility(View.GONE);
        /*multButton.animate().alpha(0f).setDuration(100l).withEndAction(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

        menuButton.animate().alpha(0f).setDuration(100l).withEndAction(new Runnable() {
            @Override
            public void run() {

            }
        }).start();*/

    }

    //关闭搜索框的动画
    private void closeAnim() {
        mSearchToolView.animate().alpha(0f).setDuration(500l).start();
        mSearchToolView.setVisibility(View.GONE);
        multButton.setVisibility(View.VISIBLE);
        menuButton.setVisibility(View.VISIBLE);
       /* multButton.animate().alpha(1f).setDuration(100l).withEndAction(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

        menuButton.animate().alpha(1f).setDuration(100l).withEndAction(new Runnable() {
            @Override
            public void run() {

            }
        }).start();*/
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
            Log.d(TAG, "onActivityResult: 结果失败");
            if (requestCode == 2020) {
                cancelUpLoad();//未选择任何文件，消费掉callback。
            }
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
        if (requestCode == 2020) {
            if (fileUploadCallBack == null) {
                return;
            }
            Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
            if (result != null) {
                fileUploadCallBack.onReceiveValue(new Uri[]{result});
            } else {
                Log.d(TAG, "onActivityResult: 选择文件结果，result是null");
                fileUploadCallBack.onReceiveValue(new Uri[]{});
            }
            fileUploadCallBack = null;
            //fileUploadCallBack.onReceiveValue(CustomWebchromeClient.FileChooserParams.parseResult(2020,data));
            Log.d(TAG, "onActivityResult: " + data.getData().toString());
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
                SavePNG_copyText png_copyText = new SavePNG_copyText(MainActivity.this);

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
    private void implControlexplorer() {
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
                    PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
                    PrintDocumentAdapter adapter = tmp.createPrintDocumentAdapter(tmp.getTitle() + TimeProcess.getTime() + ".pdf");
                    PrintAttributes attributes = new PrintAttributes.Builder()
                            .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                            .setResolution(new PrintAttributes.Resolution("id", Context.PRINT_SERVICE, 200, 200))
                            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                            .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                            .build();
                    printManager.print(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + tmp.getTitle() + TimeProcess.getTime() + ".pdf", adapter, attributes);

                }
            };
        }

    }

    /**
     * webview对于
     * <a href="http://www.google.com" target="_blank">new window</a>
     * 这种形式的tag，会要求起一个新窗口打开，而要截获这个请求，则可以在webChromeClient的onCreateWindow()回调函数中进行处理，
     */
    private void implOpenWendow(){
        if (mOpenWindowInterface ==null){
            mOpenWindowInterface =new newWebviewWindowImpl();
        }
        mWebViewManager.setOpenNewWindow(mOpenWindowInterface);
    }

    /**
     * webvie请求打开新窗口，
     * 实现接口
     */
    class newWebviewWindowImpl implements OpenWindowInterface{

        @Override
        public WebView OpenWindow(Context url) {
            mWebViewManager.stop(current);
            f1.removeView(mWebViewManager.getTop(current));
            CustomAWebView tmp=mWebViewManager.newWebview(++current,url,MainActivity.this);
            f1.addView(mWebViewManager.getTop(current));
            return tmp;
        }
    }

    /**
     * 上传文件方法
     */
    private void implFileUpload() {
        if (iupload == null) {
            iupload = new FileUploads();
        }
        mWebViewManager.setFileupload(iupload);
    }

    /**
     * 文件上传接口的实现
     */
    class FileUploads implements FileUpload {
        boolean defUploadMode = PreferenceTools.getBoolean(getApplicationContext(), WebviewConf.uploadMode, true);

        @Override
        public boolean upload(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            cancelUpLoad();//清理一次请求，防止上一次的没有消费掉而出错
            fileUploadCallBack = filePathCallback;
            try {
                if (!defUploadMode) {
                    Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                    intent2.addCategory(Intent.CATEGORY_OPENABLE);
                    intent2.setType("*/*");
                    startActivityForResult(Intent.createChooser(intent2, "上传文件"), 2020);
                } else {
                    Intent intent = fileChooserParams.createIntent();
                    Log.d(TAG, "onShowFileChooser: type:" + intent.getType() + " action:" + intent.getAction() + " category:" + intent.getCategories());
                    startActivityForResult(Intent.createChooser(intent, "上传文件"), 2020);
                }

            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                return true;
            }
            return true;


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