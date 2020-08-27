package com.example.kiylx.ti.ui.base;

import androidx.annotation.LayoutRes;
import androidx.annotation.MenuRes;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.kiylx.ti.R;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/12 9:24
 * packageName：com.example.kiylx.ti.ui.base
 * 描述：仅有一个toolbar和一个recyclerview。toolbar中只有一个searchview的菜单。
 * 可以由子类添加自定义的menu项。
 * 提供了监听menuItem的方法,提供了对searchview的调整和控制监听
 * recyclerview: histories_recyclerView
 */
public abstract class BaseRecy_search_ViewActivity extends BaseActivity implements ActionMode.Callback {
    protected ConstraintLayout layout;
    protected SearchView searchView;
    protected ActionMode actionMode;

    @Override
    @LayoutRes
    protected int layoutId() {
        return R.layout.activity_search_recyview;
    }

    /**
     * 重写此方法提供actionmode的menu
     */
    @MenuRes
    protected int actionModeMenuId() {
        return 0;
    }

    @Override
    protected Toolbar setToolbar() {
        return findViewById(R.id.only_a_searchtoolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.only_search_tool_menu, menu);
        addOtherMenu(menu);//可以由子类添加额外的菜单项
        searchView = (SearchView) menu.findItem(R.id.only_a_searchview).getActionView();
        searchControl(searchView);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ListenItemClick(item);//让子类监听menuItem项
        return super.onOptionsItemSelected(item);
    }

    /**
     * @param menu 此activity提供的menu。
     *             此方法可以由子类添加新的菜单项
     */
    protected void addOtherMenu(Menu menu) {
    }

    /**
     * @param item 此activity中的menu中的item。
     *             此方法提供子类监听item使用
     */
    protected void ListenItemClick(MenuItem item) {
    }

    /**
     * @param searchView 允许子activity对searchview进行调整和控制
     */
    protected abstract void searchControl(SearchView searchView);

    protected void addSomeView(View v) {
        getRootView();
        layout.addView(v);
    }

    protected ConstraintLayout getRootView() {
        if (layout == null) {
            layout = findViewById(R.id.base_rec_root);
        }
        return layout;
    }

    /**
     * 获取actionmode
     */
    protected void getActionMode() {
        if (actionMode == null) {
            actionMode = startSupportActionMode(this);
        }
    }

    /**
     * 隐藏actionmode
     */
    protected void hideActionMode(){
        if (actionMode!=null){
            actionMode.finish();
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(actionModeMenuId(), menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        actionMode = null;
    }
}
