package com.example.kiylx.ti.db.bookmarkdb.bookmarkfolder;

public final class BookmarkFolderDbSchema {
    private BookmarkFolderDbSchema(){}
    public static final class FolderTable {
        public static final String NAME="BookmarkFolderTab";

        public static final class childs {
            public static final String FOLDER = "folderName";
            public static final String UUID="UUID";
            public static final String PARENTUUID="parentUUID";
        }
    }
}
