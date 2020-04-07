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

    /**
     * 回到上一级文件夹
     */
    public void goBack() {
        lists.clear();
        lastPath=backStack.pop();
        lists = manager.getList(lastPath);
        updateUI();
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
                            if (clickAfter != null) {
                                clickAfter.after(data.getFilePath());
                            }
                            backStack.push(lastPath.trim());//把当前层级的路径字符串副本压入栈中
                            Log.d(TAG, "onClick: " + data.getFilePath());
                            lastPath=data.getFilePath();//把当前路径字符串改为点击的那个文件/文件夹的路径
                            if ((new File(lastPath)).isFile()){
                                //如果这是文件，就不更新视图了
                                return;
                            }
                            lists = manager.getList(lastPath);
                            updateUI();
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
}
