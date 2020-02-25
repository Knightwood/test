package com.example.kiylx.ti.myInterface;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/24 15:34
 */
public interface EditTextInterface {

    /**
     * @param s 字符串
     *          s是搜索引擎字符串，此接口用于实现单选
     *          获取被点击的item的字符串，然后再跟旧有的比较，实现单选
     */
    void changeSelect(String s);

    /**
     * @param olds 搜索引擎字符串，此接口用于更新搜索引擎字符串
     */
    void editText(String olds);

    /**
     * @param s 被点击item的搜索引擎字符串
     *          此接口用于删除一个item
     */
    void deleteItem(String s);
}
