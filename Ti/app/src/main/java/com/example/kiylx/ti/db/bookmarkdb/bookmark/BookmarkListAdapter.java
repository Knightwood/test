package com.example.kiylx.ti.db.bookmarkdb.bookmark;

import android.view.View;

import com.crystal.customview.baseadapter1.BaseAdapter;
import com.crystal.customview.baseadapter1.BaseHolder;
import com.example.kiylx.ti.R;
import com.example.kiylx.ti.model.WebPage_Info;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/12 21:15
 * packageName：com.example.kiylx.ti.mvp.presenter
 * 描述：
 */
public class BookmarkListAdapter extends BaseAdapter<WebPage_Info, BaseHolder<WebPage_Info>> {
    public BookmarkListAdapter(List<WebPage_Info> list) {
        super(list);
    }

    @Override
    protected int getItemViewResId(int viewType) {
       switch (viewType){
           case BookmarkKind.folder:
               break;
           case BookmarkKind.bookmark:
               break;
       }
        return R.layout.history_item;
    }

    @Override
    public int getViewType(WebPage_Info bean) {
        return bean.getWEB_feature() == -2 ? BookmarkKind.folder : BookmarkKind.bookmark;
    }

    @Override
    public BaseHolder<WebPage_Info> createHolder(View itemView, int viewType) {
        return new BaseHolder<>(itemView);
    }

    @Override
    public void bind(BaseHolder<WebPage_Info> holder, WebPage_Info data, int viewType) {
        holder.setImageResource(R.id.Bookmarkimage, R.drawable.ic_bookmark_border_black_24dp)
                .setText(R.id.itemTitle, data.getTitle())
                .setText(R.id.itemurl, data.getUrl())
                .setImageResource(R.id.more_setting, R.drawable.morevert);
        holder.setOnIntemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        }, data);
    }

    static class BookmarkKind {
        public static final int folder = 1;
        public static final int bookmark = 2;
    }
}
