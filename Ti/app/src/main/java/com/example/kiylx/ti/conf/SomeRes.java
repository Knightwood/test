package com.example.kiylx.ti.conf;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/14 10:21
 * <p>
 * 提供一些资源定义
 * <p>
 * 定义设置相关的key值，使用这些key值获取或修改相应的preference
 */
public class SomeRes {
    //=================================资源名称，多个地方会使用这些名称=========================================//
    public static final String homePage = "主页";//默认主页名称
    public static final String default_homePage_value = "about:newTab";
    public static final String default_homePage_url = "file:///android_asset/newTab.html";
    public static final String PCuserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.106 Safari/537.36";
    public static final String defaultBookmarkFolder = "未分类";//默认的书签文件夹名称
    public static final int UPDATE_LIST = 13;//下载管理（downloadManager）在被调用下载完成方法或取消下载方法时，使用hander发送消息，这是message.what的值
    public static int downloadLimit = 5;//默认下载任务数量限制
    public static int downloadThreadNum = 8;//默认下载线程数
    public static String preference_conf_1 = "conf01";//sharedPreference的名称

//默认的搜索引擎
    public static String baidu="http://www.baidu.com/s?wd=";
    public static String bing="https://cn.bing.com/search?q=";
    public static String sougou="https://www.sogou.com/web?query=";
    public static String google="https://www.google.com/search?q=";
    public static String miji="https://mijisou.com/search?q=";

    public static String SearchViewStyle="searchview_style";//搜索页面样式，false是旧的样式，true是新样式
    public static String searchMatcher="search_matcher";//是否使用搜索匹配，false是不启用，true是启用
}
