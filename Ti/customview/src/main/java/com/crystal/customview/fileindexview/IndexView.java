package com.crystal.customview.fileindexview;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.customview.baseadapter1.BaseAdapter;
import com.crystal.customview.baseadapter1.BaseHolder;
import com.crystal.customview.R;
import com.crystal.customview.tools.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/6 12:43
 * IndexView用于展示file列表，本质上是一个recycleView。
 * 其根据设置的action决定显示哪些种类的file，并在点击item时触发clickafter接口发出通知。
 * 可以提供复制粘贴，压缩解压缩的功能时展示file列表
 */
public class IndexView extends LinearLayout {
    private static final String TAG = "IndexView";
    private Stack<String> backStack;
    private String currentPath = null;
    private SelectAction action = SelectAction.NONE;
    private String[] filterArray = null;//筛选特定文件类型的数组
    private static final String realRootPath = Environment.getExternalStorageDirectory().getPath();//根目录的路径

    private static boolean showHidden = false;

    RecyclerView rootView;
    View itemView;
    List<FileInfo> lists;
    FileInfoAdapter adapter;
    ClickAfter clickAfter;

    public IndexView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.base_recyclerview, this);
        initView(context);
    }

    public IndexView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.base_recyclerview, this);
        initView(context);
    }

    private void initView(Context context) {
        rootView = findViewById(R.id.recycler_view);
        itemView = findViewById(R.id.item_1);
        rootView.setLayoutManager(new LinearLayoutManager(context));
    }

    /**
     * @param path   在此路径展示文件列表
     * @param action 选择文件或是选择文件夹
     * @param filter 只显示特定几种类型的文件
     */
    public void initData(String path, @Nullable SelectAction action, boolean showHidden, @Nullable String... filter) {
        backStack = new Stack<>();//此时回退栈是空的
        this.showHidden = showHidden;
        this.action=action;

        if (filter != null)
            this.filterArray = filter;
        lists = getChildrenList(path, this.filterArray);//获得indexview 初始目录

        if (path == null || path.equals(realRootPath)) {
            currentPath = realRootPath;//把lastPath设置为根目录的路径
        } else {
            String tmp = path;
            do {
                File file = new File(tmp);
                tmp = file.getParent();
                if (tmp == null)
                    break;
                backStack.push(tmp.trim());
                LogUtil.d(TAG, "压入栈的，当前的父路径" + tmp);
            } while (!tmp.equals(realRootPath));
        }

        updateUI();
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    /**
     * @param showHidden 是否显示隐藏文件
     */
    public void setShowHidden(boolean showHidden) {
        IndexView.showHidden = showHidden;
        getChildrenList(currentPath, filterArray);
        updateUI();
    }

    /**
     * @return 当前层级路径，初始化时是根目录的路径
     */
    public String getCurrentPath() {
        if (currentPath == null) {
            currentPath = Environment.getExternalStorageDirectory().getPath();//存储根目录的路径
        }
        return currentPath;
    }

    /**
     * 回到上一级文件夹，
     * 取出栈中的字符串，这个字符串就是当前目录的父路径。
     */
    public void goBack() {
        if (!backStack.empty()) {
            lists.clear();
            currentPath = backStack.pop();//取出上一层级路径
            LogUtil.d(TAG, "当前路径，返回时" + currentPath);
            lists = getChildrenList(currentPath, filterArray);
            updateUI();
            if (clickAfter != null) {
                //返回回到上一层级的路径
                clickAfter.after(currentPath);
            }
        }

    }

    private void updateUI() {
        if (adapter == null) {
            adapter = new FileInfoAdapter(lists);
            rootView.setAdapter(adapter);
        } else {
            adapter.setData(lists);
            adapter.notifyDataSetChanged();
        }
    }

    public SelectAction getAction() {
        return action;
    }

    public void setAction(SelectAction action) {
        this.action = action;
    }


    private class FileInfoAdapter extends BaseAdapter<FileInfo, BaseHolder<FileInfo>> {

        public FileInfoAdapter(List<FileInfo> list) {
            super(list);
        }

        @Override
        public BaseHolder<FileInfo> createHolder(View v) {
            return new BaseHolder<>(v);
        }

        @Override
        public int itemResId() {
            return R.layout.file_item;
        }

        @Override
        public void bind(BaseHolder<FileInfo> holder, FileInfo data) {
            holder.setImageResource(R.id.myFileImage, data.getImgId())
                    .setText(R.id.myFileName, data.getFileName())
                    .setOnIntemClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String dataFilePath = data.getFilePath();//被点击当前的item，获取被点击的item的路径，
                            LogUtil.d(TAG, "点击时lastpath: " + currentPath+"\n"+"被点击的："+dataFilePath);

                            if ((new File(dataFilePath)).isFile()) {
                                //如果这是文件，就不更新视图，直接返回
                                if (clickAfter != null && action == SelectAction.FILE) {
                                    //如果是选择文件，在点击文件时会发出通知
                                    clickAfter.after(dataFilePath);
                                }
                                return;
                            }

                            if (clickAfter != null ) {
                                //如果是选择文件夹，在点击文件时会发出通知
                                clickAfter.after(dataFilePath);
                            }
                            backStack.push(currentPath.trim());//把当前目录层级的路径字符串副本压入栈中，此时还没有进入点击的文件夹
                            LogUtil.d(TAG, "点击的文件夹或文件路径: " + dataFilePath);
                            currentPath = dataFilePath;//把当前路径字符串改为点击的文件/文件夹的路径
                            lists = getChildrenList(currentPath, filterArray);//获取点击的文件夹的子目录
                            updateUI();//更新视图
                        }
                    }, data);
        }
    }

    /**
     * 传出点击某一项或回退到某一层级时的文件夹的路径被触发，用于通知当前被选择的路径
     */
    public interface ClickAfter {
        void after(String path);
    }

    public void setClickAfter(ClickAfter after) {
        this.clickAfter = after;
    }


    /**
     * @param parentPath 当前的，传入的路径
     * @return file的子file列表，如果传入的路径不存在或这是文件将返回null
     */
    public static List<FileInfo> getChildrenList(String parentPath, String[] filter) {
        int imgId;
        File rootFile;
        List<FileInfo> infoList = new ArrayList<>();
        List<String> fileFilter = Arrays.asList(filter);
        int rank = 0;//字符串“.”的位置

        if (parentPath == null) {//获取根目录
            rootFile = Environment.getExternalStorageDirectory();
        } else {//获取特定目录下的文件
            rootFile = new File(parentPath);
            if (!rootFile.exists() || rootFile.isFile()) {
                LogUtil.d(TAG, "getChildrenList: 路径不存在或这是文件");
                return infoList;
            }
        }
        //遍历文件，返回FileInfo
        for (File file : rootFile.listFiles()) {
            String fileName = file.getName();
            //LogUtil.d(TAG, "子列表" + fileName);
            if (!showHidden && fileName.startsWith(".")) {//如果设置不显示隐藏文件,跳过此次
                continue;
            }
            if (file.isDirectory()) {
                imgId = R.drawable.folder;
            } else {
                rank = fileName.lastIndexOf(".");//最后一个"."的位置，用于找出后缀
                String suffix = fileName.substring(rank == -1 ? fileName.length() : rank);//防止字符串没有"."而返回-1超出字符串索引
                if (!suffix.isEmpty()) {
                   /* if (fileFilter != null && !fileFilter.contains(suffix)){
                        //筛选列表不为空且文件有后缀
                        //若是筛选列表中不包含当前的类型，跳过回合，不将其添加到infoList。
                        continue;
                    }*/
                    imgId = getImg(suffix);
                } else {
                    imgId = R.drawable.unknow;
                }
            }

            infoList.add(new FileInfo(imgId, file.getName(), file.getAbsolutePath()));
        }
        return infoList;
    }

    public static int getImg(String suffix) {
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

    /**
     * 选择文件，选择文件夹,普通的显示而不进行选择
     */
    public enum SelectAction {
        FILE, FOLDER, NONE
    }

}
