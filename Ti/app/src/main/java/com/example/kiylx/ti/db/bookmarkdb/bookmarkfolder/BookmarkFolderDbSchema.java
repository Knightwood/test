package com.example.kiylx.ti.db.bookmarkdb.bookmarkfolder;

public class BookmarkFolderDbSchema {

    public static final class FolderTable {
        public static final String NAME="BookmarkFolderTab";

        public static final class childs {
            public static final String FOLDER = "folderName";
            public static final String UUID="uuid";
            public static final String PARENTUUID="parentUuid";
        }
    }
}
