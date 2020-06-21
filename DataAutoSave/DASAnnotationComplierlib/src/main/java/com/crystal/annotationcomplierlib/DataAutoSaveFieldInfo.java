package com.crystal.annotationcomplierlib;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/20 17:32
 * 被注解的元素的信息
 */
public class DataAutoSaveFieldInfo {
    private String fieldName;//被注解的元素名称
    private String dataName;//注解中的dataName字段
    private String filedType;//类型

    public DataAutoSaveFieldInfo(String fieldName, String dataName, String filedType) {
        this.fieldName = fieldName;
        this.dataName = dataName;
        this.filedType = filedType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getDataName() {
        return dataName;
    }

    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    public String getFiledType() {
        return filedType;
    }

    public void setFiledType(String filedType) {
        this.filedType = filedType;
    }

    public String getFiledKey(){
        return dataName==null||dataName.equals("") ? fieldName: dataName;
    }
}
