package com.example.kiylx.ti.ui.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.kiylx.ti.db.bookmarkdb.bookmark.BookmarkDBcontrol;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.contract.base.BaseContract;
import com.example.kiylx.ti.mvp.presenter.BookmarkManagerPresenter;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.tool.networkpack.NetBroadcastReceiver;
import com.example.kiylx.ti.xapplication.Xapplication;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.UUID;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/12 17:28
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseContract.View {
    private static final String TAG="BaseActivity";
    public NetBroadcastReceiver receiver;
    private BaseLifecycleObserver lifecycleObserver;
    @Nullable
    protected Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutId());

        LogUtil.d(TAG,"onCreate");
        initView(savedInstanceState);
        initActivity(lifecycleObserver,savedInstanceState);
        initViewAfter(savedInstanceState);
    }

    @CallSuper
    protected void initView(Bundle savedInstanceState) {
        toolbar = getToolbar();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    protected abstract void initActivity(BaseLifecycleObserver observer, Bundle savedInstanceState);

    @CallSuper
    protected void initViewAfter(Bundle savedInstanceState) {

    }


    /**
     * @return 提供布局中的toolbar
     */
    protected Toolbar getToolbar() {
        return null;
    }

    /**
     * 设置toolbar的标题
     *
     * @param title    标题
     * @param subTitle 子标题
     */
    protected void setToolbarTitle(String title, String subTitle) {
        if (getSupportActionBar() != null) {
            if (title != null) {
                getSupportActionBar().setTitle(title);
            }
            if (subTitle != null) {
                getSupportActionBar().setSubtitle(subTitle);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    public <T extends View> T f(int resId) {
        return (T) findViewById(resId);
    }

    /**
     * @return 提供activity的布局id
     */
    @LayoutRes
    protected abstract int layoutId();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unRegisterBroadCast();
    }

    /**
     * 注册广播，监听网络变化
     */
    public void registerBroadCast() {
        receiver = new NetBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(receiver, intentFilter);//注册广播
    }

    public void unRegisterBroadCast() {
        unregisterReceiver(receiver);
    }

    @Override
    public void showProgressDialog(String content) {

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public ProgressDialog getProgressDialog(String content) {
        return null;
    }

    @Override
    public void showTipDialog(String content) {

    }

    @Override
    public void showConfirmDialog(String msn, String title, String confirmText, DialogInterface.OnClickListener confirmListener) {

    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showInfoToast(String message) {

    }

    @Override
    public void showSuccessToast(String message) {

    }

    @Override
    public void showErrorToast(String message) {

    }

    @Override
    public void showWarningToast(String message) {

    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showLoginPage() {

    }

    @Override
    public void share(String s) {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, s);//消息
        i.putExtra(Intent.EXTRA_SUBJECT, "分享");//消息包含的主题行
        startActivity(i);
    }

    @Override
    public void saveBookmark(WebPage_Info info) {
        info.setBookmarkFolderUUID(BookmarkManagerPresenter.DefaultBookmarkFolder.uuid);
        info.setUuid(UUID.randomUUID());
        BookmarkDBcontrol.get(Xapplication.getInstance()).insertBookmark(info);
    }

    /**
     * @param view snackbar的显示宿主
     * @param actionText snackbar右侧显示的文字
     * @param text snackbar中间显示的文字
     * @param args 要传递给点击actiontext时调用的方法的参数
     */
    @Override
    public void showSnackbar(View view, String actionText, String text, Object... args) {
        Snackbar.make(view, text, BaseTransientBottomBar.LENGTH_LONG).setAction(actionText, v -> chickSnackBar(args)).show();
    }

    /**
     * 点击snackbar时被调用的行为
     * @param object
     */
    protected void chickSnackBar(Object... object){

    }
}
