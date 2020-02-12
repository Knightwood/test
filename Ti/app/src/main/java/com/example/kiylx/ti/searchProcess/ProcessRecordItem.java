package com.example.kiylx.ti.searchProcess;

import com.example.kiylx.ti.corebase.WebPage_Info;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ProcessRecordItem {
    //用来处理字符是否是网址，以及历史记录和收藏记录放进一个数组的问题
    private static String regEx = "^((http|https)://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";

    public ProcessRecordItem() {
    }

    /**
     * @param s 字符串,是网址则直接返回s，否则返回字符串和搜索引擎拼接的网址
     * @return 处理好的网址
     */
    public static String processString(String s) {

        String engine="https://mijisou.com/search?q=";

        Pattern pattern = Pattern.compile(regEx);
        if (pattern.matcher(s).matches()){
            //如果是网址
            return s;
        }else{
            return engine+s;
        }

    }

    /**
     * @param s 传入的需要分析的字符串
     * @return 返回匹配的列表
     * 查找与s匹配的收藏记录和历史记录
     */
    public ArrayList<WebPage_Info> formatList(String s) {

        return new ArrayList<>();
    }
}
