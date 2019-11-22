package com.example.kiylx.ti.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.kiylx.ti.Discard.CustomDownloadService;
import com.example.kiylx.ti.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {

    private CustomDownloadService.DownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (CustomDownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

//绑定服务
        Intent intent = new Intent(DownloadActivity.this, CustomDownloadService.class);
        startService(intent);
        bindService(intent, connection, BIND_AUTO_CREATE);


    }

    private void updateUI() {

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
            View v = getLayoutInflater().inflate(R.layout.download_item, parent);
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

        TextView filenameView;
        TextView URlview;
        ImageButton startDownload;
        ImageButton deleteView;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            startDownload = itemView.findViewById(R.id.startDownload);
            deleteView = itemView.findViewById(R.id.deleteDownloadinfo);
            filenameView = itemView.findViewById(R.id.downloadTtitle);
            URlview = itemView.findViewById(R.id.downloadUrl);

            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
switch (v.getId()){
    case R.id.downloadTtitle:
        break;
}
        }

        public void binding(DownloadInfo info) {
        }
    }


}
