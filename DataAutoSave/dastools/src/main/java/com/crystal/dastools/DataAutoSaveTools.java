package com.crystal.dastools;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/25 22:33
 * <p>
 * 在这里使用反射，调用生成类中的保存和恢复数据方法
 */
public class DataAutoSaveTools {
    private static final String SUFFIX = "$$DataAutoSave";
    //存储反射得到的类，以减少使用反射获取类的次数
    private static LinkedHashMap<String, DASinterface<Object>> classMap = new LinkedHashMap<>();

    /**
     * @param context
     * @param bundle
     * @param restoreSuperClassData
     */
    public static void RestoreData(Object context, Bundle bundle, boolean restoreSuperClassData) {
        if (context instanceof Activity && null == bundle) {//savedInstanceState 或 intent中的数据
            bundle = ((Activity) context).getIntent().getExtras();
        }
        if (null == context || null == bundle) {
            return;
        }

        List<DASinterface<Object>> targetGenClassList = new ArrayList<>();//保存生成的类，以便调用它的方法恢复和保存数据
        GetBeGeneratedTargetClass(context, targetGenClassList, restoreSuperClassData);
        //如果要恢复父类的数据，那么这个list中的元素会大于1，因此遍历它进行处理
        if (!targetGenClassList.isEmpty()) {
            for (DASinterface<Object> targetGenClass : targetGenClassList) {
                //调用生成类中的恢复数据方法，若是方法参数 Object类型的context有父类，也可以在这里传入方法参数的 Object类型的context。因为子类中是可以访问父类中的变量的
                targetGenClass.RestoreData(context, bundle);
            }
        }

    }

    /**
     * @param context
     * @param bundle
     * @param restoreSuperClassData
     */
    public static void SaveData(Object context, Bundle bundle, boolean restoreSuperClassData) {

    }

    /**
     * @param context
     * @param map
     * @param restoreSuperClassData
     */
    private static void GetBeGeneratedTargetClass(Object context, List<DASinterface<Object>> map, boolean restoreSuperClassData) {
        Class<?> contextClass =context.getClass();

    }

    /**
     * @param className 类名
     * @return
     * 通过反射获取生成的类
     */
    private static  DASinterface<Object> getGenClass(String className){
        if (!classMap.containsKey(className)){
            try {
                Class<?> beGenClass =Class.forName(className);
                DASinterface<Object> genClass=(DASinterface<Object>)beGenClass.newInstance();
                classMap.put(className,genClass);
            }catch (ClassNotFoundException | IllegalAccessException | InstantiationException e){
                e.printStackTrace();
            }
        }
        return classMap.get(className);

    }

}
