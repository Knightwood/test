package com.crystal.customview.fileIndexView;

import android.content.Context;
import android.util.AttributeSet;
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

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/4/6 12:43
 */
public class IndexView extends LinearLayout {
    FileIndexManager manager;
    RecyclerView root;
    View itemView;
    List<FileInfo> lists;
    FileInfoAdapter adapter;
    ClickAfter clickAfter;

    public IndexView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.base_recyclerview, this);
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
        lists = manager.getList(null);//获得根目录
        root.setLayoutManager(new LinearLayoutManager(context));
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

    private class FileInfoAdapter extends BaseAdapter<FileInfo> implements OnClickListener{
        ImageView imageView;
        TextView fileNameView;
        TextView sizeView;
        TextView dateView;
        String path;


        public FileInfoAdapter(List<FileInfo> list) {
            super(list);
        }

        @Override
        public int itemResId() {
            return R.id.item_1;
        }

        @Override
        public void bind(BaseHolder holder, FileInfo data) {
            path=data.getFilePath();

            imageView = holder.getView(R.id.myFileImage);
            fileNameView=holder.getView(R.id.myFileName);
            sizeView=holder.getView(R.id.fileDate);
            dateView=holder.getView(R.id.fileDate);

            holder.setOnIntemClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickAfter!=null){
                clickAfter.after(path);
            }

        }
    }

    /**
     * 传出点击某一项时的文件夹的路径
     */
    public interface ClickAfter {
        void after(String path);
    }
}
