package com.example.kiylx.ti.downloadFragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.crystal.customview.fileIndexView.SelectFolderFragment;
import com.example.kiylx.ti.R;

public class DownloadSettingFragment extends Fragment {
    private static final String TAG="下载系列fragment";
    private View rootView;

    public static DownloadSettingFragment newInstance(){
        return new DownloadSettingFragment();
    }

    public DownloadSettingFragment(){
        super();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.download_setting_fragment,container,false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        TextView folderView=rootView.findViewById(R.id.file_path_view);
        folderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.file_path_view) {
                    Log.d(TAG, "onClick: 点击了选择文件夹view");
                    FragmentManager fm = getFragmentManager();
                    SelectFolderFragment folderFragment = new SelectFolderFragment();
                    folderFragment.show(fm, "选择下载文件夹路径");
                    folderFragment.setSendPath(new SelectFolderFragment.SendPath() {
                        @Override
                        public void send(String path) {
                            Log.d(TAG, "send: "+path);
                        }
                    });
                }
            }
        });
    }
}
