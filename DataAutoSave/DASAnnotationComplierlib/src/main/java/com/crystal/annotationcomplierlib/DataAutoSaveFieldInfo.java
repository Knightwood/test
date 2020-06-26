package com.crystal.annotationcomplierlib;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/20 17:32
 * 被注解的变量的信息
 */
public class DataAutoSaveFieldInfo {
    private String fieldName;//被注解的变量的名称
    private String fieldType;//变量的类型

    private String dataName;//注解中的dataName字段。可以自己赋予以替代变量的名称
    private boolean persistence;//是否把它持久化存储

    public DataAutoSaveFieldInfo(String fieldName, String filedType,String dataName,boolean persistence ) {
        this.fieldName = fieldName;
        this.fieldType = filedType;
        this.dataName = dataName;
        this.persistence=persistence;
    }

    /**
     * @return 返回变量的类型
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * 获取变量的名称
     * @return 若dataName没有赋予，直接返回默认的变量的名称
     */
    public String getFiledName(){
        return dataName==null||dataName.equals("") ? fieldName: dataName;
    }

    public boolean isPersistence() {
        return persistence;
    }
}
