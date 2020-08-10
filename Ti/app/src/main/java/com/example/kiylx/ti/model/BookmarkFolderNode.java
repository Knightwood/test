package com.example.kiylx.ti.model;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/6 22:24
 * packageName：com.example.kiylx.ti.model
 * 描述：描述书签文件夹的信息。
 */
public class BookmarkFolderNode {
    private String FolderName;
    private String UUID;
    private String parentUUID;

    public BookmarkFolderNode(String folderName, String UUID, String parentUUID) {
        FolderName = folderName;
        this.UUID = UUID;
        this.parentUUID = parentUUID;
    }

    public String getFolderName() {
        return FolderName;
    }

    public void setFolderName(String folderName) {
        FolderName = folderName;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getParentUUID() {
        return parentUUID;
    }

    public void setParentUUID(String parentUUID) {
        this.parentUUID = parentUUID;
    }
}
