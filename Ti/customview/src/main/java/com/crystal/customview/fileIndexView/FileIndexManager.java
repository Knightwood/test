package com.crystal.customview.fileIndexView;

import android.os.Environment;
import android.util.Log;

import com.crystal.customview.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/6 6:54
 */
public class FileIndexManager {
    private static final String TAG = "文件目录管理";
    private static FileIndexManager manager;
    private List<FileInfo>  infoList;

    public static FileIndexManager newInstance() {
        if (manager == null) {
            return new FileIndexManager();
        }else
        return manager;
    }
    private FileIndexManager(){
       infoList =new ArrayList<>();
    }

    public List<FileInfo> getList(String s) {
        int imgId;
        File rootFile;
        if (s == null) {//获取根目录
            rootFile = Environment.getExternalStorageDirectory();
        } else {//获取特定目录下的文件
            rootFile = new File(s);
            if (!rootFile.exists()||rootFile.isFile()) {
                Log.d(TAG, "getList: 路径不存在或这是文件");
                return infoList;
            }
        }
        infoList.clear();
        //遍历文件，返回FileInfo
        for (File file : rootFile.listFiles()) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                imgId = R.drawable.folder;
            } else {
                int pos = fileName.lastIndexOf(".");
                if (pos >= 0) {//有后缀
                    String suffix = fileName.substring(pos).toLowerCase();
                    imgId = getImg(suffix);
                } else {
                    imgId = R.drawable.unknow;
                }
            }

            infoList.add(new FileInfo(imgId, file.getName(), file.getAbsolutePath()));
        }
        return infoList;
    }

    private int getImg(String suffix) {
        if (suffix.contains("apk")) {
            return R.drawable.apk;
        }
        if (suffix.contains("zip")) {
            return R.drawable.zip;
        }
        if (suffix.contains("txt")) {
            return R.drawable.txt;
        }
        if (suffix.contains("pdf")) {
            return R.drawable.pdf;
        }
        if (suffix.contains("doc")) {
            return R.drawable.doc;
        }
        if (suffix.contains("ppt")) {
            return R.drawable.ppt;
        }
        if (suffix.contains("xls")) {
            return R.drawable.xls;
        }
        if (suffix.contains("html")) {
            return R.drawable.html;
        }
        if (suffix.contains("mp3")
                || suffix.contains("ape")
                || suffix.contains("flac")
                || suffix.contains("wav")
                || suffix.contains("aac")
                || suffix.contains("m4a")
        ) {
            return R.drawable.music;

        }
        if (suffix.contains("mp4")
                || suffix.contains("mkv")
                || suffix.contains("avi")
                || suffix.contains("rmvb")
                || suffix.contains("mov")
                || suffix.contains("mpeg")) {
            return R.drawable.video;
        }
        return R.drawable.file;
    }

    /*public List<FileInfo> getFatherList() {
        return stack.pop();
    }*/
}

