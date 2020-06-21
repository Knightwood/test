package com.example.kiylx.ti.tool;

import android.content.Context;
import android.text.TextUtils;

import com.example.kiylx.ti.conf.WebviewConf;
import com.example.kiylx.ti.mvp.model.WebPage_Info;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ProcessUrl {
    //用来处理字符是否是网址，以及历史记录和收藏记录放进一个数组的问题
    private static String regEx = "^((http|https)://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";
    private static String engine;

    public ProcessUrl() {
    }

    public ProcessUrl(Context context) {

        engine = PreferenceTools.getString(context, WebviewConf.searchengine, "https://mijisou.com/search?q=");
        /*final SearchEngineDao mDao= SearchEngine_db_Util.getDao(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mDao.getItem(true).isEmpty()){
                    engine="http://www.baidu.com/s?wd=";
                    SearchEngineEntity engineEntity=new SearchEngineEntity();
                    engineEntity.setUrl("http://www.baidu.com/s?wd=");
                    engineEntity.setCheck_b(true);
                    mDao.insert(engineEntity);
                }else{
                    engine=mDao.getItem(true).get(0).getUrl();
                }

            }
        }).start();*/
    }

    /**
     * @param s 字符串,是网址则直接返回s，否则返回字符串和搜索引擎拼接的网址
     * @return 处理好的网址
     */
    public static String processString(String s,Context context) {

        //String engine = "https://mijisou.com/search?q=";
        engine = PreferenceTools.getString(context, WebviewConf.searchengine, "https://mijisou.com/search?q=");
        Pattern pattern = Pattern.compile(regEx);
        if (pattern.matcher(s).matches()) {
            //正则表达式可以判断是不是网址，再由converKeywordLoadOrSearch(s)处理，补全格式。
            return converKeywordLoadOrSearch(s);
        } else {
            return engine + s;
        }

    }

    public static Boolean testPex(String s) {
        Pattern pattern = Pattern.compile(regEx);
        return pattern.matcher(s).matches();
    }

    public static final String HTTP = "http://";
    public static final String HTTPS = "https://";
    public static final String FILE = "file://";

    /**
     * 将关键字转换成最后转换的url
     *
     * @param keyword
     * @return 补全后的网址
     */
    public static String converKeywordLoadOrSearch(String keyword) {
        keyword = keyword.trim();

        if (keyword.startsWith("www.")) {
            keyword = HTTP + keyword;
        } else if (keyword.startsWith("ftp.")) {
            keyword = "ftp://" + keyword;
        }

        boolean containsPeriod = keyword.contains(".");
        boolean isIPAddress = (TextUtils.isDigitsOnly(keyword.replace(".", ""))
                && (keyword.replace(".", "").length() >= 4) && keyword.contains("."));
        boolean aboutScheme = keyword.contains("about:");
        boolean validURL = (keyword.startsWith("ftp://") || keyword.startsWith(HTTP)
                || keyword.startsWith(FILE) || keyword.startsWith(HTTPS))
                || isIPAddress;
        boolean isSearch = ((keyword.contains(" ") || !containsPeriod) && !aboutScheme);

        if (isIPAddress
                && (!keyword.startsWith(HTTP) || !keyword.startsWith(HTTPS))) {
            keyword = HTTP + keyword;
        }

        String converUrl;
        if (isSearch) {
            try {
                keyword = URLEncoder.encode(keyword, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            //converUrl = "http://www.baidu.com/s?wd=" + keyword + "&ie=UTF-8";
            converUrl = engine + keyword + "&ie=UTF-8";
        } else if (!validURL) {
            converUrl = HTTP + keyword;
        } else {
            converUrl = keyword;
        }
        return converUrl;
    }


    /**
     * @param s 传入的需要分析的字符串
     * @return 返回匹配的列表
     * 查找与s匹配的收藏记录和历史记录
     */
    public ArrayList<WebPage_Info> formatList(String s) {

        return new ArrayList<>();
    }

    /**
     * @param s    待处理的字符串
     * @param flag 分割标志
     * @return 返回s最后出现的flag和它后面的字符
     * <p>
     * 比如：s: abcd.f
     * flag: "."
     * return: ".f"
     */
    public static String splitString(String s, String flag) {
        return s.substring(s.lastIndexOf(flag));
    }
}
