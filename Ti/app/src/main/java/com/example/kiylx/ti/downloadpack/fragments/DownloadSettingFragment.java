package com.example.kiylx.ti.downloadpack.fragments;

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

import com.crystal.customview.fileindexview.IndexView;
import com.crystal.customview.fileindexview.SelectFolderFragment;
import com.crystal.customview.numberpickerview.numberPicker;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.tool.LogUtil;
import com.example.kiylx.ti.tool.preferences.PreferenceTools;
import com.example.kiylx.ti.conf.WebviewConf;

import static com.example.kiylx.ti.conf.SomeRes.rootPath;

public class DownloadSettingFragment extends Fragment {
    private static final String TAG="下载系列fragment";
    private View rootView;
    private TextView filePathView;//显示下载路径的view
    private numberPicker threadView;
    private numberPicker downloadLimitView;
    private String showedPath;//filePathView显示的路径

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
        filePathView=rootView.findViewById(R.id.file_path_view);
        threadView=rootView.findViewById(R.id.thread_value);
        downloadLimitView=rootView.findViewById(R.id.limit_value);

        threadView.setCurrent(PreferenceTools.getInt(getActivity(),WebviewConf.defaultDownloadthread,8));
        downloadLimitView.setCurrent(PreferenceTools.getInt(getActivity(),WebviewConf.defaultDownloadlimit,3));

        showedPath=PreferenceTools.getString(getActivity(),WebviewConf.defaultDownloadPath,rootPath);
        filePathView.setText(showedPath);

        threadView.setOnInputNumberListener(new numberPicker.OnInputNumberListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(int num) {
                PreferenceTools.putInt(getActivity(),WebviewConf.defaultDownloadthread,num);
            }
        });
        downloadLimitView.setOnInputNumberListener(new numberPicker.OnInputNumberListener() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(int num) {
                PreferenceTools.putInt(getActivity(),WebviewConf.defaultDownloadlimit,num);
            }
        });

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
                    LogUtil.d(TAG, "onClick: 点击了选择文件夹view");
                    FragmentManager fm = getParentFragmentManager();
                    SelectFolderFragment folderFragment = SelectFolderFragment.getInstance(showedPath, IndexView.SelectAction.FOLDER,false);
                    folderFragment.show(fm, "选择下载文件夹路径");
                    folderFragment.setSendPath(path -> {
                        LogUtil.d(TAG, "send: "+path);
                        filePathView.setText(path);
                        showedPath=path;
                        PreferenceTools.putString(getActivity(), WebviewConf.defaultDownloadPath,path);
                    });
                }
            }
        });
    }

}
