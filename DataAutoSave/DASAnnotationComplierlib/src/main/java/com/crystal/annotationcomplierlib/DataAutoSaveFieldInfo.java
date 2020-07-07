package com.crystal.annotationcomplierlib;

import java.util.Random;
import java.util.UUID;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/20 17:32
 * 被注解的变量的信息
 */
public class DataAutoSaveFieldInfo {
    private String fieldName;//被注解的变量的名称
    private String fieldType;//变量的类型
    private String topLevelClassName;//这个变量的类可能继承于其他的类，所以找到它继承自最开始的类,用于bundle存储
    private String unErasureTypeClassName;//用于持久化存储,还保留泛型的类型名称

    private String dataName;//注解中的dataName字段。可以自己赋予以替代变量的名称
    private boolean persistence;//是否把它持久化存储
    private boolean useBundle;
    private String key;

    public DataAutoSaveFieldInfo(String fieldName, String filedType, String topLevelClassName, String unErasuseTypeClassName, String dataName) {
        this.fieldName = fieldName;
        this.fieldType = filedType;
        this.topLevelClassName = topLevelClassName;
        this.unErasureTypeClassName = unErasuseTypeClassName;
        this.dataName = dataName;
        if (key == null)
            this.key = UUID.randomUUID().toString();
    }


    /**
     * @return 返回变量的类型
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * 获取变量的名称
     *
     * @return 若dataName没有赋予，直接返回默认的变量的名称
     */
    public String getFiledName() {
        return dataName == null || dataName.equals("") ? fieldName : dataName;
    }

    public boolean isPersistence() {
        return persistence;
    }

    public String getTopLevelClassName() {
        return topLevelClassName;
    }

    public void setTopLevelClassName(String topLevelClassName) {
        this.topLevelClassName = topLevelClassName;
    }

    public void setPersistence(boolean persistence) {
        this.persistence = persistence;
    }

    public boolean isUseBundle() {
        return useBundle;
    }

    public void setUseBundle(boolean useBundle) {
        this.useBundle = useBundle;
    }

    public String getUnErasureTypeClassName() {
        return unErasureTypeClassName;
    }

    public String getKey() {

        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
