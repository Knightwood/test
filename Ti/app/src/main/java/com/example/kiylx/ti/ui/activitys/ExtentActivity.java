package com.example.kiylx.ti.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.kiylx.ti.R;

public class ExtentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extent);
        initToolbar();
    }

    /**
     * 添加tolbar
     */
    private void initToolbar() {
        Toolbar extToolbar=(Toolbar) findViewById(R.id.toolbar_jsadd);
        setSupportActionBar(extToolbar);
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

    /**
     * 从文件夹中选择zip文件，然后解压至特定文件夹。
     */
    private void insertJs() {

    }
}
