package com.example.kiylx.ti.adapters.bookmarkadapter;

import android.view.View;

import com.crystal.customview.baseadapter1.BaseHolder;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.tool.LogUtil;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/25 7:25
 * packageName：com.example.kiylx.ti.adapters.bookmarkadapter
 * 描述：
 */
public class bookmarkFolderHolder extends BaseHolder<WebPage_Info> {
    private static final String TAG = "BookmarkFolderHolder";

    public bookmarkFolderHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(WebPage_Info data) {
        super.bind(data);
        if (data.isSelected()) {
            setImageResource(R.id.Bookmarkimage, R.drawable.ic_check_circle_black_24dp);
        } else {
            setImageResource(R.id.Bookmarkimage, R.drawable.ic_folder_black_24dp);
        }

        setText(R.id.itemTitle, data.getTitle())
                .setImageResource(R.id.more_setting, R.drawable.ic_chevron_right_black_24dp);
        getView(R.id.itemurl).setVisibility(View.GONE);

        LogUtil.d(TAG, "书签文件夹bind" + data.getUuid());
    }

    @Override
    public void setOnIntemClickListener(View.OnClickListener listener) {
        super.setOnIntemClickListener(listener);
        getView(R.id.itemTitle).setOnClickListener(listener);
    }

    @Override
    public void setOnIntemLongClickListener(View.OnLongClickListener listener) {
        super.setOnIntemLongClickListener(listener);
        getView(R.id.itemTitle).setOnLongClickListener(listener);
    }
}
