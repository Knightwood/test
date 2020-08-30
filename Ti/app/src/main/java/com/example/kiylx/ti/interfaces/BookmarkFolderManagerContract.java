package com.example.kiylx.ti.interfaces;

import com.example.kiylx.ti.model.WebPage_Info;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/29 14:55
 * packageName：com.example.kiylx.ti.interfaces
 * 描述：
 */
public interface BookmarkFolderManagerContract {
    void clickBack();

    boolean getBackStack();

    void enterFolder(String uuid);

    String getCurrentPath();

    String getUpperLevel();

    List<WebPage_Info> getFolderIndex(String uuid, boolean b);

    void createFolder(@NotNull String name, @NotNull String parentUUID);

    void deleteFolder(@NotNull String uuid, boolean deleteBookmark);

    void changeFolderName(@NotNull String uuid, @NotNull String newName);

    List<WebPage_Info> getBookmarkFolderList();

    String queryFolderName(String uuid);

    void destroy();
}
