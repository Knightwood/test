package com.example.kiylx.ti.ui.activitys;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiylx.ti.conf.ActivityCode;
import com.example.kiylx.ti.databinding.HomepageSettingBinding;
import com.example.kiylx.ti.databinding.ToolboxBinding;
import com.example.kiylx.ti.downloadpack.DownloadActivity;
import com.example.kiylx.ti.model.ShowPicMode;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.presenter.BookmarkManagerPresenter;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.base.BaseWebviewActivity;
import com.example.kiylx.ti.webview32.CustomAWebView;
import com.example.kiylx.ti.xapplication.Xapplication;
import com.example.kiylx.ti.tool.preferences.DefaultPreferenceTool;
import com.example.kiylx.ti.tool.SomeTools;
import com.example.kiylx.ti.tool.preferences.PreferenceTools;
import com.example.kiylx.ti.conf.SomeRes;
import com.example.kiylx.ti.ui.fragments.dialogfragment.MultPage_Dialog;
import com.example.kiylx.ti.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class MainActivity extends BaseWebviewActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private long mExitTime;//拿来判断按返回键间隔
    private Boolean isOpenedSearchText = false;//用来指示在webview页面上文本搜索有没有展开，按下返回键时如果这个是true，就把文本搜索收起来

    private TextView mTextView;//主界面的工具栏里的搜索框
    private View matcheTextView;//搜索webview文字的搜索框
    private MultPage_Dialog md;//多窗口dialogFragment
    private ImageView menuButton;//底部菜单按钮
    private ImageView multButton;//多窗口按钮
    private ProgressBar bar;//网页加载进度条
    private int currentMenu = 0;//第一层就是0，按下工具按钮展现的页面是1
    private HomepageSettingBinding homepageSettingBinding;
    private ToolboxBinding toolboxBinding;
    private BottomSheetBehavior behavior;
    private BottomSheetBehavior behavior2;


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        firstInstall();
        mTextView = findViewById(R.id.search_edittext);
        multButton = findViewById(R.id.mult_button);
        menuButton = findViewById(R.id.menu_button);
        bar = findViewById(R.id.webviewProgressBar);

        homepageSettingBinding = DataBindingUtil.bind(findViewById(R.id.setbottom));
        homepageSettingBinding.setClicklister(this);
        toolboxBinding = DataBindingUtil.bind(findViewById(R.id.setbottom_tool));
        toolboxBinding.setClicklister(this);

        View bottomSheet = findViewById(R.id.bottom_sheet_main);
        View bottomSheet2 = findViewById(R.id.bottom_sheet_tool);
        behavior = BottomSheetBehavior.from(bottomSheet);//主设置界面
        behavior2 = BottomSheetBehavior.from(bottomSheet2);//工具箱界面
    }

    @Override
    protected void initViewAfter(Bundle savedInstanceState) {
        super.initViewAfter(savedInstanceState);
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    /**
     * 测试上下文
     */
    private void testContext() {
        LogUtil.d(TAG, "测试上下文，getApplication得到的是:  " + getApplicationContext());
        LogUtil.d(TAG, "测试上下文，Xapplication.getInstance()得到的是：   " + Xapplication.getInstance());
    }

    /**
     * @param progress 网页加载进度
     *                 更新进度条进度
     *                 如果进度是-1或是100时，隐藏进度条
     */
    @Override
    public void updateWebviewProgress(int progress) {
        if (bar == null) {
            bar = findViewById(R.id.webviewProgressBar);
        }
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
            if (isExpendedBottomset()) {
                closeBottomSheet();
            } else if (isOpenedSearchText) {
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reload_webview:
                reload();
                break;
            case R.id.back1:
            case R.id.toolButton:
                LogUtil.d(TAG, "onClick: 切换菜单");
                collapsed(2);
                break;
            case R.id.findtext:
                openPageInsideSearchView();
                break;
            case R.id.share:
                sharing(mWebViewManager.getTop(current).getUrl());
                break;
            case R.id.pcMode:
                boolean b = homepageSettingBinding.pcMode.isChecked();
                if (b)
                    usePcMode();
                stateManager.setPcMode(b);
                break;
            case R.id.hideSelf:
                boolean b1 = homepageSettingBinding.hideSelf.isChecked();
                stateManager.setDontRecordHistory(b1);
                break;
            case R.id.addBookmark:
                CustomAWebView info=mWebViewManager.getTop(current);
                addtobookmark(info.getUrl(),info.getTitle(),false );
                break;
            case R.id.menu:
                startSetting();
                break;
            case R.id.button_download:
                openDownloadView();
                break;
            case R.id.button_bookmark:
                startBookmarked();
                break;
            case R.id.button_history:
                startHistory();
                break;
            case R.id.save_pdf:
                printPdf();
                break;
            case R.id.save_mht:
                saveWeb();
                break;
            case R.id.show_pic:
                changePicMode();
                break;

        }
        closeBottomSheet();

        LogUtil.d(TAG, "onClick: " + v.getId());
    }

    /**
     * 更改图片模式，影响网页图片显示
     */
    private void changePicMode() {
        String[] menu = new String[]{"总是显示", "仅WIFI下显示", "禁止显示"};
        int itemId = 0;
        ShowPicMode showPicMode = ShowPicMode.valueOf(DefaultPreferenceTool.getStrings(Xapplication.getInstance(), "showPicatureMode", "ALWAYS"));
        switch (showPicMode) {
            case ALWAYS:
                itemId = 0;
                break;
            case JUSTWIFI:
                itemId = 1;
                break;
            case DONT:
                itemId = 2;
                break;
        }
        new android.app.AlertDialog.Builder(this).setSingleChoiceItems(menu, itemId, (dialog, which) -> {
            switch (which) {
                case 0:
                    SomeTools.getXapplication().getStateManager().setShowPicMode(ShowPicMode.ALWAYS);
                    break;
                case 1:
                    SomeTools.getXapplication().getStateManager().setShowPicMode(ShowPicMode.JUSTWIFI);
                    break;
                case 2:
                    SomeTools.getXapplication().getStateManager().setShowPicMode(ShowPicMode.DONT);
                    break;
            }
        }).setTitle("图片模式").create().show();
    }

    @Override
    protected void openDownloadView() {
        Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
        startActivity(intent);
    }

    private void startHistory() {
        Intent history_intent = new Intent(MainActivity.this, HistorysActivity.class);
        startActivity(history_intent);
    }

    /**
     * 启动设置页面
     */
    private void startSetting() {
        Intent settingIntent = new Intent(MainActivity.this, Setting2Activity.class);
        startActivity(settingIntent);
    }

    /**
     * 启动书签页面
     */
    private void startBookmarked() {
        Intent Bookmark_intent = new Intent(MainActivity.this, BookmarkManagerActivity.class);
        startActivity(Bookmark_intent);
    }

    /**
     * @param pos 当前webview在列表中的位置
     *            <p>
     *            工具栏的的文字更新，获取当前浏览网页的标题，设置到底栏
     */
    @Override
    protected void provideCurrentWebviewTitle(int pos) {
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
    @Override
    public void mult_dialog(View v) {
        super.mult_dialog(v);
        FragmentManager fm = getSupportFragmentManager();
        md = new MultPage_Dialog();
        md.show(fm, "fragment_multPage_dialog");
    }

    /**
     * 打开底部菜单界面
     */
    public void buttomMenu(View v) {
        collapsed(1);
    }

    /**
     * @param i 若是1，只展示和隐藏底部菜单；若是2，在底部菜单与工具箱菜单之间切换
     *          切换两个底部菜单的打开隐藏状态
     */
    private void collapsed(int i) {
        if (i == 1) {
            collapsedMainSet();
        }
        if (i == 2) {
            collapsedMainSet();
            collpasedToolBox();
        }

        LogUtil.d(TAG, "展开或关闭菜单" + behavior.getState());

    }

    /**
     * 展示或隐藏底部菜单
     */
    private void collapsedMainSet() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    /**
     * 展示或隐藏底部工具箱菜单
     */
    private void collpasedToolBox() {
        if (behavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior2.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            behavior2.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    /**
     * @return 若是有底部菜单是打开状态，返回true
     */
    private boolean isExpendedBottomset(){
        if (behavior2.getState() == BottomSheetBehavior.STATE_EXPANDED||behavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
            return true;
        }
        return false;
    }

    /**
     * 关闭底部菜单
     */
    private void closeBottomSheet() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
        if (behavior2.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior2.setState(BottomSheetBehavior.STATE_HIDDEN);

        }
    }

    /**
     * 搜索框代码,由activity_main.xml中的搜索框调用,展示搜索框,输入文本，载入网页
     */
    @Override
    public void openSearchView(View v) {
        super.openSearchView(v);
        Intent intent = new Intent(MainActivity.this, ContentToUrlActivity.class);
        intent.putExtra("text_or_url", mTextView.getText().toString());
        //把当前网页网址传进去
        startActivityForResult(intent, 21);
    }

    /**
     * 把webview的页内文本搜索收起来
     */
    @Override
    protected void closeMatchesText() {
        super.closeMatchesText();
        matcheTextView.setVisibility(View.INVISIBLE);
        isOpenedSearchText = false;
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
                SearchTextGoHead();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchTexBback();
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
                    findAllText(content);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ActivityCode.openSearchActivity) {
            //把DoSearchActivity的requestCode定义为21
            if (data != null) {
                mWebViewManager.getTop(current).loadUrl(data.getStringExtra("text_or_url"));
                mTextView.setText("正在载入...");
                LogUtil.d(TAG, "onActivityResult: 被触发" + data.getStringExtra("text_or_url"));
            }
            isOpenedEdit = false;
        }
    }

    @Override
    protected void chickSnackBar(Object... object) {
        super.chickSnackBar();
        Intent i = EditBookmarkActivity.newInstance(new WebPage_Info.Builder((String) object[0]).title((String) object[1]).build(), this, BookmarkManagerPresenter.DefaultBookmarkFolder.folderName);
        startActivity(i);
    }

    @Override
    public void addtobookmark(String url, String title, boolean openEditView) {
        if (openEditView){
            chickSnackBar(url,title);
        }else{
            saveBookmark(new WebPage_Info.Builder(url).title(title).build());
            showSnackbar(findViewById(R.id.layout), "编辑", "编辑书签",url,title);
        }

    }

    @Override
    public void openPageInsideSearchView() {
        //打开搜索webview内的文本
        openMatchesTextView();
    }


}
/*
*
* View decorView = getActivity().getWindow().getDecorView();
                //隐藏NavigationBar
                    int option = SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                    decorView.setSystemUiVisibility(option);
* */