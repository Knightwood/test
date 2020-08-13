package com.example.kiylx.ti.model;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/6 22:24
 * packageName：com.example.kiylx.ti.model
 * 描述：描述书签文件夹的信息。
 * node:
 *      url:nul
 *      title:文件夹的名称
 *      uuid：文件夹自身的uuid
 *      bookmarkFolderUUID：文件夹所属的父级uuid
 *      web_feature=-2:标识这是一个文件夹而不是书签
 */
public class BookmarkFolderNode extends WebPage_Info{
    public BookmarkFolderNode(String folderName, String UUID, String parentUUID) {
        super(new Builder("nul").title(folderName).uuid(UUID).bookmarkFolderUUID(parentUUID));
         setWEB_feature(-2);//标识为书签文件夹
    }

    public String getFolderName() {
        return getUUID();
    }

    public void setFolderName(String folderName) {
        setTitle(folderName);
    }

    public String getUUID() {
        return getUuid();
    }

    public void setUUID(String UUID) {
        setUUID( UUID);
    }

    public String getParentUUID() {
        return getBookmarkFolderUUID();
    }

    public void setParentUUID(String parentUUID) {
        setBookmarkFolderUUID(parentUUID);
    }
}
