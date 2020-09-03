package com.example.kiylx.ti.downloadpack.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadpack.DownloadActivity;
import com.example.kiylx.ti.downloadpack.adapter.complete.DownloadCompleteAdapter;
import com.example.kiylx.ti.downloadpack.adapter.downloading.DownloadListAdapter;
import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;
import com.example.kiylx.ti.downloadpack.dinterface.DownloadClickMethod;
import com.example.kiylx.ti.downloadpack.db.DownloadEntity;
import com.example.kiylx.ti.downloadpack.db.DownloadInfoDatabaseUtil;
import com.example.kiylx.ti.model.EventMessage;
import com.example.kiylx.ti.tool.LogUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadFinishFragment extends RecyclerViewBaseFragment {
    private static final String TAG = "下载完成fragment";
    protected static List<DownloadInfo> completeList;

    public DownloadFinishFragment() {
        super();
    }

    @Override
    protected void initRecyclerview() {

        super.initRecyclerview();
        mAdapter = new DownloadCompleteAdapter(getDataList());
        ((DownloadCompleteAdapter) mAdapter).setInterface(controlMethod);
        viewContainer.setAdapter(mAdapter);
    }

    /**
     * @return 提供list数据
     */
    @Override
    public List<DownloadInfo> getDataList() {
        if (completeList == null) {
            completeList = new ArrayList<>();
        }
        if (controlMethod != null)
            controlMethod.getAllComplete(completeList);
        LogUtil.d(TAG, "下载完成列表: " + completeList.size());
        return completeList;

   /*     new Thread(new Runnable() {
            @Override
            public void run() {
                List<DownloadEntity> list=new ArrayList<>();
                list=DownloadInfoDatabaseUtil.getDao(getActivity()).getAll();
                for (DownloadEntity e:list
                ) {
                    LogUtil.d(TAG, "getDataList: 数量"+e.filename);
                }
            }
        }).start();
*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(EventMessage message) {
        if (message.getType() == 1) {
            LogUtil.d(TAG, "eventbus接受到了事件，正在更新视图");
            update();
        }
    }

    /**
     * 更新recyclerview
     */
    private void update() {
        controlMethod.getAllComplete(completeList);
        mAdapter.notifyDataSetChanged();
    }

    void itemPopMenu(View v, DownloadInfo info) {
        PopupMenu menu = new PopupMenu(getContext(), v);
        MenuInflater inflater = menu.getMenuInflater();
        inflater.inflate(R.menu.open_download_file, menu.getMenu());
        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.openFile:
                        break;
                    case R.id.deleteFile:
                        break;
                }
                return true;
            }
        });
        menu.show();

    }

    public Intent getFileOpenIntent(String path, String Suffix) {
        File file = new File(path);
        if (file.exists()) {
            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, Suffix);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "文件不存在或已删除", Toast.LENGTH_LONG).show();
        }
        return null;
    }
}
