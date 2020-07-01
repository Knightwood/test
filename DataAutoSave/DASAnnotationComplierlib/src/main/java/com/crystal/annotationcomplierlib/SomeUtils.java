package com.crystal.annotationcomplierlib;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/20 17:43
 */
public enum SomeUtils {
    INSTANCE;


    public static final List<String> SUPPORTED_PERSISTENCE_FIELD_TYPE = Arrays.asList(
            "int", "long", "float", "double", "byte", "short",
            "java.lang.String[]", "boolean[]", "int[]", "double[]", "float[]", "long[]", "byte[]", "char[]", "short[]",
            "boolean",
            "java.lang.String",
            "char",
            "java.util.HashMap<java.lang.String,java.lang.String>",
            "java.util.Set<java.lang.String>",
            "java.util.List<java.lang.Integer>",
            "java.util.List<java.lang.Long>",
            "java.util.List<java.lang.String>"
    );

    /**
     * interface类型
     */
    static final List<String> SUPPORTED_INTERFACE_TYPE = Arrays.asList(
            "android.os.Parcelable", "java.io.Serializable", "java.lang.CharSequence"
    );

    /**
     * 支持的,可以使用注解的字段类型
     */
    static final List<String> SUPPORTED_FIELD_TYPE = Arrays.asList(
            "java.lang.String", "int", "boolean", "double", "float", "long",
            "byte", "char", "short",
            "android.os.Parcelable", "java.io.Serializable", "android.os.Bundle", "java.lang.CharSequence",
            "java.util.ArrayList", "java.lang.String[]", "int[]", "boolean[]", "double[]", "float[]", "long[]",
            "byte[]", "char[]", "short[]", "android.os.Parcelable[]", "java.lang.CharSequence[]"
    );

    /**
     * 使用bundle中存储数据时的方法名称
     */
    static final List<String> PUT_DATA_PRE_CODE = Arrays.asList(
            "putString", "putInt", "putBoolean", "putDouble", "putFloat", "putLong",
            "putByte", "putChar", "putShort", "putParcelable", "putSerializable", "putBundle", "putCharSequence",
            "putSerializable",
            "putStringArray", "putIntArray", "putBooleanArray", "putDoubleArray", "putFloatArray", "putLongArray",
            "putByteArray", "putCharArray", "putShortArray", "putParcelableArray", "putCharSequenceArray"
    );
    /**
     * 什么类型的被注解的的使用相应的方法
     * <p></p>
     * LinkedHashMap是继承于HashMap，是基于HashMap和双向链表来实现的。
     * HashMap无序；LinkedHashMap有序，可分为插入顺序和访问顺序两种。如果是访问顺序，那put和get操作已存在的Entry时，都会把Entry移动到双向链表的表尾(其实是先删除再插入)。
     * LinkedHashMap存取数据，还是跟HashMap一样使用的Entry[]的方式，双向链表只是为了保证顺序。
     * LinkedHashMap是线程不安全的。
     */
    static final LinkedHashMap<String, String> Bundle_PUT_DATA_PRE_CODE_MAP = new LinkedHashMap<String, String>();
    static final HashMap<String, String> Persistence_Method_SUFFIX = new HashMap<>();
    private static final List<String> PERSISTENCE_SUPPORTED_ARRAY = Arrays.asList("boolean[]", "int[]", "double[]", "float[]", "long[]", "byte[]", "char[]", "short[]");
    private static final List<String> PERSISTENCE_SUPPORTED_Num = Arrays.asList("int", "long", "float", "double", "byte", "short");
    /**
     * 初始化linkedhashmap，用于存储数据时，依据key的类型使用value的字符串拼接生成方法
     */
    void init() {
        if (!Bundle_PUT_DATA_PRE_CODE_MAP.isEmpty()) {
            return;
        }
        Iterator<String> iterator = SUPPORTED_FIELD_TYPE.iterator();
        Iterator<String> iterator1 = PUT_DATA_PRE_CODE.iterator();
        while (iterator.hasNext()) {
            Bundle_PUT_DATA_PRE_CODE_MAP.put(iterator.next(), iterator1.next());
        }

        if (Persistence_Method_SUFFIX.isEmpty()) {
            for (String m : PERSISTENCE_SUPPORTED_ARRAY) {
                Persistence_Method_SUFFIX.put(m, "Arrays");
            }
            for (String num : PERSISTENCE_SUPPORTED_Num) {
                Persistence_Method_SUFFIX.put(num, "Num");
            }
            Persistence_Method_SUFFIX.put("java.lang.String[]", "StringArray");
            Persistence_Method_SUFFIX.put("boolean", "Boolean");
            Persistence_Method_SUFFIX.put("char", "String");
            Persistence_Method_SUFFIX.put("java.lang.String", "String");
            Persistence_Method_SUFFIX.put("java.util.Set<java.lang.String>", "StringSet");
            Persistence_Method_SUFFIX.put("java.util.HashMap<java.lang.String,java.lang.String>", "HashMap");
            Persistence_Method_SUFFIX.put("java.util.List<java.lang.Integer>", "IntList");
            Persistence_Method_SUFFIX.put("java.util.List<java.lang.Long>", "LongList");
            Persistence_Method_SUFFIX.put("java.util.List<java.lang.String>", "StringList");
            //Method_SUFFIX.put("java.lang.Object", "Bean");
            //beanList
            //bean
        }


    }


}
