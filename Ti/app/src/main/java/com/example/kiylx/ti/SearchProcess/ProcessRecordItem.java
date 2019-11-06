package com.example.kiylx.ti.SearchProcess;

import com.example.kiylx.ti.Corebase.WebPage_Info;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ProcessRecordItem {
    //用来处理字符是否是网址，以及历史记录和收藏记录放进一个数组的问题
    private String regEx = "^((http|https)://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";

    public ProcessRecordItem() {
    }

    public boolean processString(String s) {
        //判断是否输入的文字是一个网址，是的话返回的true
        Pattern pattern = Pattern.compile(regEx);
        return pattern.matcher(s).matches();
    }

    public ArrayList<WebPage_Info> formatList(String s) {
//查找与形参匹配的收藏记录和历史记录
        return new ArrayList<>();
    }
}
