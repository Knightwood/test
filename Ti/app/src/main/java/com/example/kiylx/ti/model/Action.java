package com.example.kiylx.ti.model;

public enum Action {
    /*
    WebViewManager发出更新时，有添加，删除，和更新内容三种行为.
    在这里定义这三种行为
    2020.2.22：新增GETALL,FIND用于“搜索引擎数据库”获取数据的行为*/
    ADD,DELETE,UPDATEINFO,GETALL,FIND
}
