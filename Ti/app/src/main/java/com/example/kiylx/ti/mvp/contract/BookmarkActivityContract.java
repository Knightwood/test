package com.example.kiylx.ti.mvp.contract;

import com.example.kiylx.ti.model.BookmarkFolderNode;
import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.base.BaseContract;
import com.example.kiylx.ti.tool.KeyValue;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/10 22:47
 * packageName：com.example.kiylx.ti.mvp.contract
 * 描述：
 */
public interface BookmarkActivityContract extends BaseContract.View {

    /**
     * 更新界面
     * @param keyValue 包含有文件夹list和书签list的键值对
     */
    void UpdateUI(KeyValue<BookmarkFolderNode, WebPage_Info> keyValue);
}
