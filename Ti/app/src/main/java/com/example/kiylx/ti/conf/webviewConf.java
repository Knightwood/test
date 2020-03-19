package com.example.kiylx.ti.conf;

/**
 * 创建者 kiylx
 * 创建时间 2020/3/17 15:12
 * 关于webview的一些设置的key。
 */
public class WebviewConf {
    //=================================preference的key值=========================================//

    public static String useCustomHomepage = "home_page_default";//是否使用自定义主页key
    public static String homepageurl = "home_page_url";//自定义主页网址key

    public static String searchengine = "search_engine";//默认搜索引擎key
    public static String searchengineList="search_engine_list";//搜索引擎列表key

    public static String customDownload = "custom_download_listener";//是否使用软件内置下载器key

    public static String userAgent = "user_agent";//默认浏览器标识key
    public static String userAgentList="useragent_list";//浏览器标识列表key

    public static String cleanData = "clean_data";//清除数据key
    public static String textZoom = "text_zoom";//字体缩放key
    public static String textZoomList = "text_zoom_list";//字体缩放列表key

    public static String resumeData = "resume_data";//是否打开恢复上次网址key

}
