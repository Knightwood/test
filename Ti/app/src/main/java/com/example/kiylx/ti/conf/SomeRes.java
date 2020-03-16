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

    
    //=================================preference的key值=========================================//
    public static int downloadLimit = 5;//默认下载任务数量限制
    public static int downloadThreadNum = 8;//默认下载线程数
    public static String preference_conf_1 = "conf01";//一个sharedPreference的名称
    public static String useDefaultHomepage = "home_page_default";//使用默认主页
    public static String searchengine = "search_engine";//搜索引擎
    public static String customDownload = "custom_download_listener";//使用软件内置下载器
    public static String userAgent = "user_agent";//浏览器标识
    public static String cleanData = "clean_data";//清楚数据
    public static String textZoom = "text_zoom";//字体缩放
    public static String homepage = "home_page";//主页
    public static String resumeData = "resume_data";//打开恢复上次网址
}
