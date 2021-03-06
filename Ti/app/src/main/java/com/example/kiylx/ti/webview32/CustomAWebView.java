package com.example.kiylx.ti.webview32;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.interfaces.ActionSelectListener;
import com.example.kiylx.ti.interfaces.HandleClickedLinks;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.webview32.nestedjspack.NestedJsCode;
import com.example.kiylx.ti.webview32.nestedjspack.Suggestion;

import java.util.ArrayList;
import java.util.List;


public class CustomAWebView extends WebView {

    private static String TAG = "CustomActionWebView";

    ActionMode mActionMode;
    List<String> mActionList = new ArrayList<>();
    ActionSelectListener mActionSelectListener;
    private HandleClickedLinks handlerClick;
    private int touchX = 0, touchY = 0;


    public CustomAWebView(Context context) {
        super(context);
        longClick();
    }

    public CustomAWebView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomAWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        longClick();
    }


    /**
     * 长按事件，通过HitTestResult获取当前触碰的地方的类型，可以满足长按文本选中，长按图片保存等功能。
     * 识别类型，并通过拦截触摸事件获取的x，y值以及定义的处理连接的接口，来处理长按事件
     */
    private void longClick() {
        LogUtil.d(TAG, "longClick: 触发长按");
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                HitTestResult result = getHitTestResult();
                if (result == null) {
                    return false;
                }
                int type = result.getType();
                String extra = result.getExtra();
                switch (type) {
                    case HitTestResult.SRC_IMAGE_ANCHOR_TYPE://带图片的超链接
                    /*if (handlerClick!=null&&extra!=null&&URLUtil.isValidUrl(extra)){
                        handlerClick.onImgLink(touchX,touchY,result.getType(),extra);

                    }return true;*/
                    case HitTestResult.SRC_ANCHOR_TYPE://超链接
                        if (handlerClick != null && extra != null && URLUtil.isValidUrl(extra)) {
                            handlerClick.onLinkSelected(touchX, touchY, result.getType(), extra);
                        }
                        return true;
                    case HitTestResult.IMAGE_TYPE://图片类型
                        if (handlerClick != null && extra != null && URLUtil.isValidUrl(extra)) {//URLUtil.isValidUrl(extra)是否是有效链接
                            handlerClick.onImgSelected(touchX, touchY, result.getType(), extra);
                        }
                        return true;
                    case HitTestResult.EDIT_TEXT_TYPE://文字
                        break;
                }

                return false;
            }
        });
    }

    /**
     * @param event 触摸事件
     * @return 获取触摸的x, y值
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        touchX = (int) event.getX();
        touchY = (int) event.getY();
        return super.onInterceptTouchEvent(event);
    }

    /**
     * @param handleClickLinks 处理长按不同的url行为的实现
     */
    public void setHandleClickLinks(HandleClickedLinks handleClickLinks) {
        this.handlerClick = handleClickLinks;
    }

    @Override
    public void saveWebArchive(String filename) {
        super.saveWebArchive(filename);
        /*BaseThread baseThread= new BaseThread(new DownloadEntity(filename.substring(filename.lastIndexOf("/"),filename.lastIndexOf(".")),filename,-2),this::insertDownloadInfo);
        baseThread.start();*/
        Toast.makeText(getContext(), "以保存至Download文件夹", Toast.LENGTH_LONG).show();
        LogUtil.d(TAG, "saveWebArchive1: " + filename);
    }

    @Override
    public void saveWebArchive(String basename, boolean autoname, @Nullable ValueCallback<String> callback) {
        super.saveWebArchive(basename, autoname, callback);
        LogUtil.d(TAG, "saveWebArchive:2 ");
    }

    //把离线保存的网页信息保存在下载记录数据库
    /*public interface Method{
        public void method(DownloadEntity entity);
    }

    class BaseThread extends Thread{
        Method method;
        DownloadEntity entity;

        public BaseThread(DownloadEntity entity,Method method){
            this.method=method;
            this.entity=entity;
        }
        @Override
        public void run() {
            method.method(entity);
        }
    }

    private void insertDownloadInfo(DownloadEntity entity){
        DownloadInfoDatabaseUtil.getDao(getContext()).insertAll(entity);
    }*/

//===============================================================================

    @Override
    public ActionMode startActionMode(ActionMode.Callback callback) {
        /*ActionMode actionMode = super.startActionMode(callback);
        return resolveActionMode(actionMode);*/
        return super.startActionMode(callback);
    }


    @Override
    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        /*ActionMode actionMode = super.startActionMode(callback, type);
        return resolveActionMode(actionMode);*/
        return super.startActionMode(callback, type);
    }

    /**
     * 处理item，处理点击
     *
     * @param actionMode
     */
    private ActionMode resolveActionMode(ActionMode actionMode) {
        if (actionMode != null) {
            final Menu menu = actionMode.getMenu();
            mActionMode = actionMode;
            menu.clear();
            for (int i = 0; i < mActionList.size(); i++) {
                menu.add(mActionList.get(i));
            }
            for (int i = 0; i < menu.size(); i++) {
                MenuItem menuItem = menu.getItem(i);
                menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        getSelectedData((String) item.getTitle());
                        releaseAction();
                        return true;
                    }
                });
            }
        }
        mActionMode = actionMode;
        return actionMode;
    }

    private void releaseAction() {
        if (mActionMode != null) {
            mActionMode.finish();
            mActionMode = null;
        }
    }

    /**
     * 点击的时候，获取网页中选择的文本，回掉到原生中的js接口
     *
     * @param title 传入点击的item文本，一起通过js返回给原生接口
     */
    private void getSelectedData(String title) {
        String js = String.format(NestedJsCode.jsmenu, title);
        /*String js = "(function getSelectedText() {" +
                "var txt;" +
                "var title = \"" + title + "\";" +
                "if (window.getSelection) {" +
                "txt = window.getSelection().toString();" +
                "} else if (window.document.getSelection) {" +
                "txt = window.document.getSelection().toString();" +
                "} else if (window.document.selection) {" +
                "txt = window.document.selection.createRange().text;" +
                "}" +
                "JSInterface.callback(txt,title);" +
                "})()";*/
            evaluateJavascript("javascript:" + js, null);
    }

    /**
     * 设置弹出action列表
     */
    public void setActionList() {
        mActionList.add("复制");
        mActionList.add("搜索");
        mActionList.add("分享");
    }

    /**
     * 设置点击回调
     *
     * @param actionSelectListener
     */
    public void setActionSelectListener(ActionSelectListener actionSelectListener) {
        this.mActionSelectListener = actionSelectListener;
    }

    /**
     * 隐藏消失Action
     */
    public void dismissAction() {
        releaseAction();
    }


    /**
     * js选中的回调接口
     */
    private class ActionSelectInterface {

        CustomAWebView mContext;

        ActionSelectInterface(CustomAWebView c) {
            mContext = c;
        }

        @JavascriptInterface
        public void callback(final String value, final String title) {
            if (mActionSelectListener != null) {
                mActionSelectListener.onClick(title, value);
                //Toast.makeText(getContext(), "触发了长按", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
