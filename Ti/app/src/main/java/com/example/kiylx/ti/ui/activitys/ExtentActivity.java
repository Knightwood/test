package com.example.kiylx.ti.ui.activitys;

import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.ui.base.BaseActivity;

/**
 * 管理js脚本
 */
public class ExtentActivity extends BaseActivity {

    @Override
    protected void initActivity(BaseLifecycleObserver observer, Bundle savedInstanceState) {

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initToolbar();
    }

    @Override
    protected Toolbar getToolbar() {
        return (Toolbar) findViewById(R.id.toolbar_jsadd);
    }

    /**
     * 添加tolbar
     */
    private void initToolbar() {
        setToolbarTitle("JS管理器",null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.js_extent_add,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.toolbar_jsadd:
                insertJs();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_extent;
    }

    /**
     * 从文件夹中选择zip文件，然后解压至特定文件夹。
     */
    private void insertJs() {

    }
}
