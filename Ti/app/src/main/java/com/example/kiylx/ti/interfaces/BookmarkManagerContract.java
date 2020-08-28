package com.example.kiylx.ti.interfaces;

import com.example.kiylx.ti.model.WebPage_Info;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/26 11:05
 * packageName：com.example.kiylx.ti.interfaces
 * 描述：
 */
public interface BookmarkManagerContract {


    void clickBack();

    boolean getBackStack();

    void enterFolder(String uuid);

    String getCurrentPath();

    String getUpperLevel();

    void getIndex(@NotNull String uuid, boolean b);

    List<WebPage_Info> getFolderIndex(String uuid, boolean b);

    void createFolder(@NotNull String name, @NotNull String parentUUID);

    void deleteFolder(@NotNull String uuid, boolean deleteBookmark);

    void deleteBookmark(String uuid);

    void changeFolderName(@NotNull String uuid, @NotNull String newName);

    void changeFolderLevel(@NotNull String uuid, @NotNull String newParentUuid);

    void insertBookmark(WebPage_Info info);

    List<WebPage_Info> getBookmarkList();

    List<WebPage_Info> getBookmarkFolderList();

    String queryFolderName(String uuid);

    void destroy();
}
