package com.example.kiylx.ti.mvp.contract;

import com.example.kiylx.ti.model.WebPage_Info;
import com.example.kiylx.ti.mvp.contract.base.BaseContract;

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
     * @param folderAndBookmark 包含有文件夹和书签的混合list
     */
    void UpdateUI(List<WebPage_Info> folderAndBookmark);
}
