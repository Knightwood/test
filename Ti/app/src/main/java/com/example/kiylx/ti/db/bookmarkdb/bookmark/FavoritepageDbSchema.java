package com.example.kiylx.ti.db.bookmarkdb.bookmark;

public class FavoritepageDbSchema {

public static final class FavoriteTable{
    public static final String NAME="Favorite_tab";

    public static final class childs{
        public static final String ID="uuid";
        public static final String TITLE="title";
        public static final String url="url";
        public static final String BookmarkFolderUuid ="folderUUID";//这是指向bookmarkfoldernode中的uuid

    }
}
}
