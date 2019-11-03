package com.example.kiylx.ti.model;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.kiylx.ti.Activitys.BookmarkPageActivity;
import com.example.kiylx.ti.Activitys.HistoryActivity;
import com.example.kiylx.ti.Activitys.SettingActivity;
import com.example.kiylx.ti.R;

import static android.widget.Toast.LENGTH_SHORT;

public class ClickModel {
    private Context mContext;
    private static final String TAG="ClickModel";

        public ClickModel(Context context){
            mContext=context;
        }
        public void clicks(View v){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.findtext:
                            break;
                        case R.id.share:
                            break;
                        case R.id.pcMode:
                            break;
                        case R.id.hideSelf:
                            break;
                        case R.id.addBookmark:
                            break;
                        case R.id.menu:
                            break;
                        case R.id.button_download:
                            break;
                        case R.id.button_bookmark:
                            break;
                        case R.id.button_history:
                            break;

                    }
                    Log.d(TAG, "onClick: "+v.getId());
                    startHistory();
                }
            });
        }
    private void startHistory(){
        Intent history_intent = new Intent(mContext, HistoryActivity.class);
       mContext.startActivity(history_intent);
    }
    /**
     * 启动设置页面
     */
    private void startSetting(){
        mContext.startActivity(new Intent(mContext, SettingActivity.class));

    }

    /**
     * 启动书签页面
     */
    private void startBookmarked(){
        Intent Bookmark_intent = new Intent(mContext, BookmarkPageActivity.class);
        mContext.startActivity(Bookmark_intent);
    }

    /**
     * 分享
     */
    private void sharing(){

    }
    private void find(){

    }
    private void addtobookmark(){

    }
    private void addtoreading(){

    }
    private void download(){

    }


}
