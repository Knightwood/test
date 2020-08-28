package com.example.kiylx.ti.model;

import android.os.Build;

import com.crystal.customview.baseadapter1.BeanSelect;
import com.example.kiylx.ti.ui.base.BaseActivity;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class WebPage_Info implements BeanSelect {
    //收集网页的信息，用于展示多窗口以及记录历史记录
    private String uuid;//书签本身的uuid，标识其唯一性
    private String title;
    private String url;
    private int WEB_feature;
    private String date;
    private String bookmarkFolderUUID;//这是书签所属的文件夹的名称，它不是显示的name，是文件夹节点的uuid
    private int progress;//网页加载进度
    /*WEB_feature：0，主页,不计入历史记录;，url指定为about:newTab
     * 所以除了0以外的flags都是可以计入历史记录的
     * 1，将载入网址，可以计入历史记录;
     * -1：网页被收藏;
     * -2这是表示用作书签文件夹的标识，uuid是文件夹的uuid，bookmarkFolderUUID是文件夹的父文件夹uuid，title是文件夹名称，url设为“nul”
     * */
    //feature 标志，0或者-1都可以，0在不计入收藏时，-1是计入收藏时写入的，但是在收藏记录那，压根就没用过。所以，以后要删除这个标志

private boolean selectFlag;//在多选或单选中，此标志位可表示相应的itemview是否被选择

    /**
     * @param URL
     */
    @Deprecated
    public WebPage_Info(String URL) {
        this("", URL, null, 0, null);
    }

    /**
     * @param title
     * @param URL
     * @param date
     */
    @Deprecated
    public WebPage_Info(String title, String URL, String date) {
        this(title, URL, null, date);
    }

    /**
     * @param uuid
     * @param title
     * @param url
     * @param bookmarkFolder
     */
    @Deprecated
    public WebPage_Info(String uuid, String title, String url, String bookmarkFolder) {
        this.uuid = uuid;
        this.title = title;
        this.url = url;
        this.bookmarkFolderUUID = bookmarkFolder;
    }

    /**
     * @param title              标题
     * @param url                网址
     * @param bookmarkFolderUUID 收藏网页时标识所属文件夹。
     * @param web_feature        标识是否被收藏了  -标识网页是属于什么类的，收藏，历史，为加载网址。以半废弃
     *                           使用的地方：1，在webviewManager中会写为0，传入webpageinfo到webViewInfo_Manager类
     *                           2，在收藏里没有使用，所以默认给一个0就可以
     * @param date               网页加载时间
     */
    @Deprecated
    public WebPage_Info(String title, String url, String bookmarkFolderUUID, int web_feature, String date) {
        this.title = title;
        this.url = url;
        this.bookmarkFolderUUID = bookmarkFolderUUID;
        this.WEB_feature = web_feature;
        this.date = date;
    }


    /**
     * @param builder
     */
    public WebPage_Info(Builder builder) {
        this.uuid = builder.uuid;
        this.title = builder.title;
        this.url = builder.url;
        this.WEB_feature = builder.WEB_feature;
        this.date = builder.date;
        this.bookmarkFolderUUID = builder.bookmarkFolderUUID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWEB_feature() {
        return WEB_feature;
    }

    public void setWEB_feature(int WEB_feature) {
        this.WEB_feature = WEB_feature;
    }

    public void setDate(String s) {
        this.date = s;
    }

    public String getDate() {
        return this.date;
    }

    public String getBookmarkFolderUUID() {
        return this.bookmarkFolderUUID;
    }

    public void setBookmarkFolderUUID(String s) {
        this.bookmarkFolderUUID = s;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid.toString();
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }


    /**
     * @return bean对应的itemview是否被选择，若被选择，返回true
     */
     @Override
    public boolean isSelected(){
    return selectFlag;
    };

    /**
     * 设置bean对应的itemview是选择状态
     * @param selected
     */
      @Override
   public void setSelected(boolean selected){
   selectFlag=selected;
   };

    /**
     * 根据需求生成wegpage_info
     */
    public static final class Builder {
        private final String url;

        private String uuid;//书签本身的uuid，标识其唯一性
        private String title;
        private int WEB_feature;
        private String date;
        private String bookmarkFolderUUID;//这是书签所属的文件夹的名称，它不是显示的name，是文件夹节点的uuid
        private int progress;

        public Builder(String url) {
            this.url = url;
        }

        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder date(String date) {
            this.date = date;
            return this;
        }

        public Builder WEB_feature(int WEB_feature) {
            this.WEB_feature = WEB_feature;
            return this;
        }

        public Builder bookmarkFolderUUID(String bookmarkFolderUUID) {
            this.bookmarkFolderUUID = bookmarkFolderUUID;
            return this;
        }

        public Builder progress(int progress) {
            this.progress = progress;
            return this;
        }

        public WebPage_Info build() {
            return new WebPage_Info(this);
        }
    }
}
