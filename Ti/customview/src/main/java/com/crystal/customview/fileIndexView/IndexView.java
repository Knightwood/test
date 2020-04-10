package com.crystal.customview.fileIndexView;

import android.content.Context;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crystal.customview.BaseAdapter1.BaseAdapter;
import com.crystal.customview.BaseAdapter1.BaseHolder;
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
    private String lastPath;

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

        backStack = new Stack<>();
        lists = manager.getList(null);//获得根目录
        lastPath = Environment.getExternalStorageDirectory().getPath();//存储根目录的路径
        root.setLayoutManager(new LinearLayoutManager(context));
        updateUI();

    }

    public String getLastPath() {
        if (lastPath == null) {
            lastPath = Environment.getExternalStorageDirectory().getPath();//存储根目录的路径
        }
        return lastPath;
    }

    /**
     * 回到上一级文件夹，
     * 取出栈中的字符串，这个字符串就是当前目录的父路径。
     */
    public void goBack() {
        if (!backStack.empty()) {
            lists.clear();
            lastPath = backStack.pop();
            lists = manager.getList(lastPath);
            updateUI();
            if (clickAfter != null) {
                clickAfter.after(lastPath);
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

    private class FileInfoAdapter extends BaseAdapter<FileInfo> {

        public FileInfoAdapter(List<FileInfo> list) {
            super(list);
        }

        @Override
        public int itemResId() {
            return R.layout.file_item;
        }

        @Override
        public void bind(final BaseHolder holder, final FileInfo data) {

            holder.setImageResource(R.id.myFileImage, data.getImgId())
                    .setText(R.id.myFileName, data.getFileName())
                    .setOnIntemClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String currentPath=data.getFilePath();//点击的文件夹或文件的路径
                            Log.d(TAG, "点击时lastpath: "+lastPath);
                            if ((new File(currentPath)).isFile()) {
                                //如果这是文件，就不更新视图，直接返回
                                return;
                            }
                            if (clickAfter != null) {
                                clickAfter.after(currentPath);
                            }
                            backStack.push(lastPath.trim());//把当前目录层级的路径字符串副本压入栈中，此时还没有进入点击的文件夹
                            Log.d(TAG, "点击的文件夹或文件路径: " + currentPath);
                            lastPath =currentPath;//把当前路径字符串改为点击的文件/文件夹的路径
                            lists = manager.getList(lastPath);//获取点击的文件夹的子目录
                            updateUI();//更新视图
                        }
                    }, data);
        }

    }

    /**
     * 传出点击某一项时的文件夹的路径
     */
    public interface ClickAfter {
        void after(String path);
    }

    public void setClickAfter(ClickAfter after) {
        this.clickAfter = after;
    }
}
