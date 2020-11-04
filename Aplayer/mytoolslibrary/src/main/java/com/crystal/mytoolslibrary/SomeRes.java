package com.crystal.fucktoollibrary.tools;

/**
 * 创建者 kiylx
 * 创建时间 2020/9/9 22:47
 * packageName：com.crystal.fucktoollibrary.tools
 * 描述：
 */
public class SomeRes {
    public static String preference_conf_1 = "conf01";//sharedPreference的名称
    public static int downloadThreadNum = 8;//默认下载线程数
    public static int downloadLimit =3;

    public static String searchengine = "search_engine";//默认搜索引擎key
    public static String searchengineList="search_engine_list";//内置搜索引擎列表 的key
    public static String customsearchengine = "custom_search_engine";//自定义搜索引擎的key，自定义的不一定会设置为默认，所以，这一项是为了在自定义的那项不是默认值时也能记住自定义的值

    public static String userAgent = "user_agent";//默认浏览器标识key
    public static String userAgentList="useragent_list";//浏览器标识列表key
    public static String customAgent="custom_agent";//自定义的agent

    public static String defaultDownloadPath="def_download_path";//下载路径
    public static String defaultDownloadlimit="def_download_limit";//下载数量限制
    public static String defaultDownloadthread="def_download_thread";//下载线程数
}
