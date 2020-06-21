package com.crystal.annotationcomplierlib;

import java.util.ArrayList;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/21 18:17
 * 要生成的类的描述信息
 */
public class BeCreateClassInfo {
    private String classPackage;
    private String className;
    private String qualifiedName;
    //这个被生成的类的信息包含有不止一个“在类中被注解的变量”的元素
    private ArrayList<DataAutoSaveFieldInfo> fieldInfos=new ArrayList<>();

    public BeCreateClassInfo(String classPackage, String className, String qualifiedName) {
        this.classPackage = classPackage;
        this.className = className;
        this.qualifiedName = qualifiedName;
    }

    public void addField(DataAutoSaveFieldInfo field){
        fieldInfos.add(field);
    }
    public ArrayList<DataAutoSaveFieldInfo> getFields() {
        return fieldInfos;
    }

    public String getClassPackage() {
        return classPackage;
    }

    public void setClassPackage(String classPackage) {
        this.classPackage = classPackage;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }
}
