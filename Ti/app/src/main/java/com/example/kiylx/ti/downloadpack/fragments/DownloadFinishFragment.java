package com.example.kiylx.ti.downloadpack.fragments;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kiylx.ti.R;
import com.example.kiylx.ti.downloadpack.bean.DownloadInfo;
import com.example.kiylx.ti.downloadpack.dinterface.DownloadClickMethod;
import com.example.kiylx.ti.downloadpack.db.DownloadEntity;
import com.example.kiylx.ti.downloadpack.db.DownloadInfoDatabaseUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DownloadFinishFragment extends RecyclerViewBaseFragment {
    private static final String TAG="下载完成fragment";
    private static List<DownloadInfo> completeList;
    private static DownloadClickMethod controlInterface;



    public static DownloadFinishFragment newInstance(DownloadClickMethod method){
        controlInterface=method;
        return new DownloadFinishFragment();
    }

    @Override
    public int getItemResId() {
        return R.layout.history_item;
    }

    /**
     * @return 提供list数据
     */
    @Override
    public List<DownloadInfo> downloadInfoList() {
        if (completeList==null){
            completeList=controlInterface.getAllComplete();
            Log.d(TAG, "下载完成列表: "+completeList.size());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<DownloadEntity> list=new ArrayList<>();
                list=DownloadInfoDatabaseUtil.getDao(getActivity()).getAll();
                for (DownloadEntity e:list
                ) {
                    Log.d(TAG, "downloadInfoList: 数量"+e.filename);
                }
            }
        }).start();



        return completeList;
    }

    public DownloadFinishFragment(){
        super();
    }

    @Override
    public void bindItemView(View v, DownloadInfo info) {
        TextView title=v.findViewById(R.id.itemTitle);
        TextView url=v.findViewById(R.id.itemurl);
        ImageView optionView=v.findViewById(R.id.more_setting);

        title.setText(info.getFileName());
        url.setText(info.getUrl());
        optionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemPopMenu(v,info);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();

    }
   void itemPopMenu(View v,DownloadInfo info){
       PopupMenu menu=new PopupMenu(getContext(),v);
       MenuInflater inflater=menu.getMenuInflater();
       inflater.inflate(R.menu.open_download_file,menu.getMenu());
       menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {
               switch (item.getItemId()){
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
        }else {
            Toast.makeText(getContext(),"文件不存在或已删除",Toast.LENGTH_LONG).show();
        }
        return null;
    }
}
