package com.example.kiylx.ti.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;

import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.kiylx.ti.Corebase.DownloadInfo;
import com.example.kiylx.ti.DownloadCore.DownloadServices;
import com.example.kiylx.ti.Fragments.DownloadWindow;
import com.example.kiylx.ti.INTERFACE.RegisterDownloadService;
import com.example.kiylx.ti.R;


import java.util.ArrayList;
import java.util.List;

/**
 * 下载管理界面
 */
public class DownloadActivity extends AppCompatActivity {

    /**
     * 放下载的列表的recyclerview
     */
    private RecyclerView rootview;
    private DownloadViewAdapter mAdapter;
    /**
     * 存放下载的信息的列表
     */
    private List<DownloadInfo> downloadList;
    private DownloadServices.DownloadBinder downloadBinder;


    public DownloadActivity() {
        super();
        downloadList = new ArrayList<>();

        downloadList.add(new DownloadInfo("www.baidu.com/ko3"));
        downloadList.add(new DownloadInfo("www.baidu.com/ko2"));
        downloadList.add(new DownloadInfo("www.baidu.com/ko1"));
    }

    //与service通信的中间件
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadServices.DownloadBinder) service;//向下转型
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        rootview=findViewById(R.id.downloadRecyclerview);

        //downloadList=从存储中获取下载信息
        //更新视图
        updateUI();

        //开启下载服务
        //startDownoadService();
        //绑定服务.下载服务由mainActivity在点击下载窗口中的“开始”的时候开启并绑定到mainActivity，当DownloadActivity被打开始的时候，就只需要绑定下载服务。
        boundDownloadService();

        //测试开始下载任务的按钮
        Button bui=findViewById(R.id.ceshianniu);
        bui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DownloadWindow dof= DownloadWindow.getInstance(new DownloadInfo("www.baidu.com/ko"));
                //FragmentManager fragmentManager=getSupportFragmentManager();
                //dof.show(fragmentManager,"下载");
                boolean as=false;
                downloadBinder.pauseDownload(null);
            }
        });



    }

    /**
     * 开启下载服务，并绑定服务（混合绑定），以此保证服务在后台运行：
     * 即使downloadActivivty结束也可以继续在后台运行
     */
    private void startDownoadService() {
        Intent intent = new Intent(DownloadActivity.this, DownloadServices.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    /**
     * 绑定下载服务，以此控制下载服务中的下载。
     */
    private void boundDownloadService(){
        Intent intent = new Intent(DownloadActivity.this, DownloadServices.class);
        bindService(intent, connection, BIND_AUTO_CREATE);
    }

    private void updateUI() {
        rootview.setLayoutManager(new LinearLayoutManager(DownloadActivity.this));
        if (mAdapter == null) {
            mAdapter = new DownloadViewAdapter();
            mAdapter.setLists(downloadList);
        } else {
            mAdapter.setLists(downloadList);
            mAdapter.notifyDataSetChanged();
        }
        rootview.setAdapter(mAdapter);

    }

    private class DownloadViewAdapter extends RecyclerView.Adapter<DownloadViewHolder> {
        List<DownloadInfo> infos;

        public DownloadViewAdapter() {
            infos = new ArrayList<>();
        }

        /**
         * @param list 所有的下列信息
         *             赋值下载信息的列表，准备构建recyclerview视图
         */
        public void setLists(List<DownloadInfo> list) {
            this.infos = list;
        }

        @NonNull
        @Override
        public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = getLayoutInflater().inflate(R.layout.download_item, parent,false);
            return new DownloadViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
            holder.binding(infos.get(position));
        }

        @Override
        public int getItemCount() {
            return this.infos.size();
        }
    }

    private class DownloadViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        String fileName;
        String DownloadURL;

        DownloadInfo mDownloadInfo;

        TextView filenameView;
        TextView URlview;
        ImageButton startDownload;
        ImageButton deleteView;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            startDownload = itemView.findViewById(R.id.resumeDownload);
            deleteView = itemView.findViewById(R.id.deleteDownloadinfo);
            filenameView = itemView.findViewById(R.id.downloadTtitle);
            URlview = itemView.findViewById(R.id.downloadUrl);

            startDownload.setOnClickListener(this);
            deleteView.setOnClickListener(this);

        }

        @Override
        /**
         * 控制下载行为
         */
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.resumeDownload:
                    downloadBinder.resumeDownload(mDownloadInfo);
                    break;
                case R.id.deleteDownloadinfo:
                    downloadBinder.canaelDownload(mDownloadInfo);
                    break;
            }
        }

        public void binding(DownloadInfo info) {
            mDownloadInfo = info;
            fileName = info.getFileName();
            DownloadURL = info.getUrl();

        }
    }


}
