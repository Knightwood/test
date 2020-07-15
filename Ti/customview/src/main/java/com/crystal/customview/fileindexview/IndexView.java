package com.crystal.customview.fileindexview;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.customview.baseadapter1.BaseAdapter;
import com.crystal.customview.baseadapter1.BaseHolder;
import com.crystal.customview.R;

import java.io.File;
import java.util.List;
import java.util.Stack;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/6 12:43
 */
public class IndexView extends LinearLayout {
    private static final String TAG = "IndexView";
    private Stack<String> backStack;
    private String currentPath;

    FileIndexManager manager;
    RecyclerView root;
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
        root = findViewById(R.id.recycler_view);
        itemView = findViewById(R.id.item_1);
        manager = FileIndexManager.newInstance();

        backStack = new Stack<>();//此时回退栈是空的
        lists = manager.getChildrenList(null);//获得根目录
        currentPath = Environment.getExternalStorageDirectory().getPath();//把lastPath设置为根目录的路径
        root.setLayoutManager(new LinearLayoutManager(context));
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
            lists = manager.getChildrenList(currentPath);
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
            root.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private class FileInfoAdapter extends BaseAdapter<FileInfo,BaseHolder<FileInfo>> {

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
                            String dataFilePath=data.getFilePath();//被点击当前的item，获取被点击的item的路径，
                            Log.d(TAG, "点击时lastpath: "+ currentPath);
                            if ((new File(dataFilePath)).isFile()) {
                                //如果这是文件，就不更新视图，直接返回
                                return;
                            }
                            if (clickAfter != null) {
                                clickAfter.after(dataFilePath);
                            }
                            backStack.push(currentPath.trim());//把当前目录层级的路径字符串副本压入栈中，此时还没有进入点击的文件夹
                            Log.d(TAG, "点击的文件夹或文件路径: " + dataFilePath);
                            currentPath =dataFilePath;//把当前路径字符串改为点击的文件/文件夹的路径
                            lists = manager.getChildrenList(currentPath);//获取点击的文件夹的子目录
                            updateUI();//更新视图
                        }
                    }, data);
        }
    }

    /**
     * 传出点击某一项或回退到某一层级时的文件夹的路径
     */
    public interface ClickAfter {
        void after(String path);
    }

    public void setClickAfter(ClickAfter after) {
        this.clickAfter = after;
    }
}
