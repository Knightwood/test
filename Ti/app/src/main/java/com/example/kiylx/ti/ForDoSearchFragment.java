package com.example.kiylx.ti;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class ForDoSearchFragment {
    //用来处理字符是否是网址，以及历史记录和收藏记录放进一个数组的问题
private String regEx="^((http|https)://)?([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$";

    public ForDoSearchFragment() {
    }

    public boolean processString(String s){
        //判断是否输入的文字是一个网址，是的话返回的true
        Pattern pattern = Pattern.compile(regEx);
        return pattern.matcher(s).matches();
    }
    public ArrayList<WebPage_Info> formatList(String s){

        return new ArrayList<>();
    }
}
