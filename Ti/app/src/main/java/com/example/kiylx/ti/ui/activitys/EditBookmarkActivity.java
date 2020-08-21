package com.example.kiylx.ti.ui.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.conf.ActivityCode;
import com.example.kiylx.ti.db.bookmarkdb.bookmark.BookmarkDBcontrol;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.base.BaseLifecycleObserver;
import com.example.kiylx.ti.mvp.presenter.BookmarkManager;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.ui.base.BaseActivity;
import com.example.kiylx.ti.xapplication.Xapplication;

import java.util.UUID;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/18 14:21
 * packageName：com.example.kiylx.ti.ui.fragments.dialogfragment
 * 描述：
 */
public class EditBookmarkActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "添加书签的弹窗";

    private WebPage_Info beBookmarked_info;
    private BookmarkDBcontrol mDataBase;

    //private View view;
    private EditText titleView;
    private EditText urlView;
    private TextView folderView;
    private String folderName;//书签所属文件夹的名称
    private Button saveButton;


    /**
     * @param info       要添加进数据库的网页信息,url,title是必须的，bookmarkFolder可以为null，即将新添加到收藏网页，bookmarkFolder一定是null
     * @param folderName 该书签所属文件夹的名称。
     * @return bookmark_dialog
     */
    public static Intent newInstance(WebPage_Info info, Context context, @Nullable String folderName) {
        Intent intent = new Intent(context, EditBookmarkActivity.class);
        intent.putExtra("url", info.getUrl());
        intent.putExtra("title", info.getTitle());
        intent.putExtra("uuid", info.getUuid());
        intent.putExtra("parentUUID", info.getBookmarkFolderUUID());
        if (folderName == null)
            intent.putExtra("FolderName", "默认文件夹");
        else
            intent.putExtra("FolderName", folderName);
        return intent;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        urlView = findViewById(R.id.editUrl);
        titleView = findViewById(R.id.edit_title);
        folderView = findViewById(R.id.bookmarkFolderView);
        folderView.setOnClickListener(this);
        saveButton = findViewById(R.id.saveBookmark);
        saveButton.setOnClickListener(this);
    }

    @Override
    protected void initActivity(BaseLifecycleObserver observer) {
        mDataBase = BookmarkDBcontrol.get(Xapplication.getInstance());
        Intent intent = getIntent();
        if (intent != null) {
            beBookmarked_info = new WebPage_Info.Builder(intent.getStringExtra("url"))
                    .title(intent.getStringExtra("title"))
                    .uuid(intent.getStringExtra("uuid"))
                    .bookmarkFolderUUID(intent.getStringExtra("parentUUID"))
                    .build();
            folderName = intent.getStringExtra("FolderName");
        }
        setMassage();
    }

    @Override
    protected int layoutId() {
        return R.layout.edit_bookmark_layout;
    }

    /**
     * 存储书签信息
     */
    private void saveInfo() {
        beBookmarked_info.setTitle(titleView.getText().toString());
        beBookmarked_info.setUrl(urlView.getText().toString());
        if (beBookmarked_info.getTitle() == null) {
            beBookmarked_info.setTitle("  ");
        }
        if (beBookmarked_info.getBookmarkFolderUUID() == null) {
            beBookmarked_info.setBookmarkFolderUUID(BookmarkManager.DefaultBookmarkFolder.uuid);
        }
        if (beBookmarked_info.getUuid() == null) {
            beBookmarked_info.setUuid(UUID.randomUUID());
            mDataBase.insertBookmark(beBookmarked_info);
            LogUtil.d(TAG, "onClick: uuid是null");
        } else {
            LogUtil.d(TAG, "onClick: uuid不是null，所以更新数据库");
            mDataBase.updateBookmark(beBookmarked_info);
        }
        setResult(Activity.RESULT_OK);
    }

    /**
     * beBookmarked_info的信息填充到收藏对话框
     */
    private void setMassage() {
        if (beBookmarked_info==null){
            return;
        }
        titleView.setText(beBookmarked_info.getTitle());
        urlView.setText(beBookmarked_info.getUrl());
        if (folderName != null) {
            folderView.setText(folderName);
        } else {
            folderView.setText("...");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ActivityCode.selectBookmarkFolder) {
                if (data != null) {
                    this.folderName = data.getStringExtra("FolderName");
                    setMassage();
                }
            }
        }
    }

    /**
     * 打开书签文件夹activity，选择文件夹
     */
    private void selectFolder() {
        Intent intent = new Intent(this, BookmarkManagerActivity.class);
        intent.putExtra("isShowBookmarks", false);
        startActivityForResult(intent, ActivityCode.selectBookmarkFolder);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bookmarkFolderView:
                selectFolder();
                break;
            case R.id.saveBookmark:
                saveInfo();
                finish();
                break;
        }
    }

}
