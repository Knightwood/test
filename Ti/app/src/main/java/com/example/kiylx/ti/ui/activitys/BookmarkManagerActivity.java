package com.example.kiylx.ti.ui.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.BookmarkFolderNode;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.BookmarkActivityContract;
import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.presenter.BookmarkControl;
import com.example.kiylx.ti.tool.KeyValue;
import com.example.kiylx.ti.ui.base.BaseActivity;
import com.example.kiylx.ti.xapplication.Xapplication;

public class BookmarkManagerActivity extends BaseActivity implements BookmarkActivityContract {
    BookmarkControl bookmarkControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark_manager);
        bookmarkControl=BookmarkControl.getInstance(Xapplication.getInstance(),this);
    }

    @Override
    protected void initActivity(BaseLifecycleObserver observer) {

    }

    @Override
    public void UpdateUI(KeyValue<BookmarkFolderNode, WebPage_Info> keyValue) {

    }
}
