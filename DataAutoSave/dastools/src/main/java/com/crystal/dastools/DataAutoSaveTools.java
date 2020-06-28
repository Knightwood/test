package com.crystal.dastools;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/25 22:33
 * <p>
 * 在这里使用反射，调用生成类中的保存和恢复数据方法
 */
/*
这就是生成的类，context就是被注解元素所属的类的实例，它若是有父类，也是可以在子类中访问到父类里的对象的。
因此在下面的方法实现中，在保存和恢复context父类中的被注解对象时，可以传入这个context已访问到父类中的对象
* public class MainActivity$$DataAutoSave<T extends MainActivity> implements DASinterface<T> {
  @Override
  public void RestoreData(T context, Bundle bundle) {
    if (bundle.containsKey("testField"))
    context.testField=(java.lang.String) bundle.get("testField");
  }

  @Override
  public void SaveData(T context, Bundle bundle) {
    bundle.putString("testField",context.testField);
  }
}
* */
public class DataAutoSaveTools {
    private static final String SUFFIX = "$$DataAutoSave";
    //存储反射得到的类，以减少使用反射获取类的次数
    private static LinkedHashMap<String, DASinterface<Object>> classMap = new LinkedHashMap<>();
    private static final String TAG="tools";

    /**
     * @param context
     * @param bundle
     * @param isRestoreSuperClassData
     */
    public static void restoreData(Object context, Bundle bundle, boolean isRestoreSuperClassData) {
        Log.d(TAG, "restoreData: 恢复数据传入参数：  "+context.getClass().getCanonicalName());
        if (context == null) {
            Log.d(TAG, "restoreData: 恢复数据失败，context是null");
            return;
        }
        if (context instanceof Activity && null == bundle) {//savedInstanceState 或 intent中的数据
            bundle = ((Activity) context).getIntent().getExtras();
        }
        if (null == bundle) {
            Log.d(TAG, "restoreData: 恢复数据失败，bundle是null");
            return;
        }

        List<DASinterface<Object>> targetGenClassList = new ArrayList<>();//保存生成的类，以便调用它的方法恢复和保存数据
        getBeGeneratedTargetClass(context.getClass(), targetGenClassList, isRestoreSuperClassData);
        //如果要恢复父类的数据，那么这个list中的元素会大于1，因此遍历它进行处理
        if (!targetGenClassList.isEmpty()) {
            for (DASinterface<Object> targetGenClass : targetGenClassList) {
                //调用生成类中的恢复数据方法，若是方法参数 Object类型的context有父类，也可以在这里传入方法参数的 Object类型的context。因为子类中是可以访问父类中的变量的
                targetGenClass.RestoreData(context, bundle);
            }
        }

        Log.d(TAG, "restoreData: 恢复数据成功");

    }

    /**
     * @param context
     * @param bundle
     * @param isSaveSuperClassData
     */
    public static void saveData(Object context, Bundle bundle, boolean isSaveSuperClassData) {
        Log.d(TAG, "saveData: 保存数据传入参数：  "+context.getClass().getCanonicalName());
        if (context == null ||bundle == null ) {
            Log.d(TAG, "saveData: 保存数据失败");
            return;
        }

        List<DASinterface<Object>> targetGenClassList = new ArrayList<>();//保存生成的类，以便调用它的方法恢复和保存数据
        getBeGeneratedTargetClass(context.getClass(), targetGenClassList, isSaveSuperClassData);

        if (!targetGenClassList.isEmpty()) {
            for (DASinterface<Object> targetGenClass : targetGenClassList) {
                targetGenClass.SaveData(context, bundle);
            }
        }
        Log.d(TAG, "saveData: 保存数据成功");
    }

    /**
     * @param context               被注解的元素所属的类的实例获取到的Class
     * @param genClassList          获取到的生成的类的实例的list
     * @param restoreSuperClassData 是否查找父类
     *                              根据context得到生成类的名称，然后获取到这个生成类，加入list
     */
    private static void getBeGeneratedTargetClass(Class<?> context, List<DASinterface<Object>> genClassList, boolean restoreSuperClassData) {
        if (null == context) {
            return;
        }
        String genClassName = context.getName().concat(SUFFIX);//类名加上前缀就是生成类的名称
        Log.d(TAG, "getBeGeneratedTargetClass: "+genClassName);

        if (!classMap.containsKey(genClassName)) {
            DASinterface<Object> genClass = getGenClass(genClassName);
            if (genClass == null) {
                return;
            }
            classMap.put(genClassName, genClass);
            genClassList.add(genClass);
        } else {
            genClassList.add(classMap.get(genClassName));
        }
        if (restoreSuperClassData) {
            getBeGeneratedTargetClass(context.getSuperclass(), genClassList, true);
        }
    }

    /**
     * @param className 类名
     * @return 通过反射获取生成的类
     */
    private static DASinterface<Object> getGenClass(String className) {
        Log.d(TAG, "getGenClass: "+className);
        if (!classMap.containsKey(className)) {
            try {
                Class<?> beGenClass = Class.forName(className);
                DASinterface<Object> genClass = (DASinterface<Object>) beGenClass.newInstance();
                classMap.put(className, genClass);
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
        return classMap.get(className);

    }


}
