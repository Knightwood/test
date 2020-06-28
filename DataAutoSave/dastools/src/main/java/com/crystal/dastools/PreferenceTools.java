package com.crystal.dastools;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/26 16:11
 */
public class PreferenceTools {
    private static String PREFERENCE_NAME = "preference_conf_vesion1";
    Object object = new Object();

    public static final List<String> SUPPORTED_VARABLE = Arrays.asList(
            "int", "long", "float", "double", "byte", "short",
            "java.lang.String[]", "boolean[]", "int[]", "double[]", "float[]", "long[]", "byte[]", "char[]", "short[]",
            "boolean",
            "java.lang.String",

            "char",
            "java.util.HashMap",
            "java.util.List",
            "java.lang.Object"
    );

    public static final List<String> SUPPORTED_METHOD = Arrays.asList(
            "putString", "getString",
            "putInt", "getInt",
            "putLong", "getLong",
            "putFloat", "getFloat",
            "putBoolean", "getBoolean",
            "putHashMap", "getHashMap",

            "putStringList", "getStringList",
            "putIntList", "getIntList",
            "putLongList", "getLongList",
            "putListBean", "getListBean",

            "putArrays", "getArrays",

            "saveBean", "getBean");

    public static final HashMap<String, String> SUPPORT_CODE_SUFFIX = new HashMap<>();


    /**
     * @param context 上下文
     *                清空SharedPreferences
     */
    public static void clearAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        sp.edit().clear().apply();
    }

    /**
     * @param context context
     *                <p> "java.lang.String[]", "boolean[]","int[]", "double[]", "float[]", "long[]", "byte[]", "char[]", "short[]",
     *                </p>
     */
    public static <T> void putArrays(Context context, String key, T[] arrsys) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, arrsys.length);
        for (int i = 0; i < arrsys.length; i++) {
            editor.putString(key + i, String.valueOf(arrsys[i]));
        }
        editor.apply();
    }

    /**
     * @param context context
     *               <p> "java.lang.String[]", "boolean[]","int[]", "double[]", "float[]", "long[]", "byte[]", "char[]", "short[]",
     */
    public static void getArrays(Context context, String key, String[] arrsys) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        int length = settings.getInt(key, 0);
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                arrsys[i] = settings.getString(key + i, "");
            }
        }
    }

    /**
     * @param context
     * @param key
     * @param value
     */
    public static <T> void putNum(Context context, String key, T value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        String res = String.valueOf(value);
        editor.putString(key, res);
        editor.apply();
    }

    /**
     * @param context
     * @param key
     * @return 支持的类型
     * "double", "byte", "char", "short",
     * 存储时使用了string，取出来时也返回string类型
     */
    public static String getNum(Context context, String key) {
        return getString(context, key);
    }
    //"java.lang.String[]","char[]","boolean[]",

    public static <T> void putStringArray(Context context, String key, T[] arrsys) {

    }


    /**
     * put string preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putString(Context context, String key, String value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     * get string preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or null. Throws ClassCastException if there is a preference with this
     * name that is not a string
     * @see #getString(Context, String, String)
     */
    public static String getString(Context context, String key) {
        return getString(context, key, null);
    }

    /**
     * get string preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a string
     */
    public static String getString(Context context, String key, String defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getString(key, defaultValue);
    }

    /**
     * put int preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     * get int preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a int
     * @see #getInt(Context, String, int)
     */
    public static int getInt(Context context, String key) {
        return getInt(context, key, -1);
    }

    /**
     * get int preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a int
     */
    public static int getInt(Context context, String key, int defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getInt(key, defaultValue);
    }

    /**
     * put long preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a long
     * @see #getLong(Context, String, long)
     */
    public static long getLong(Context context, String key) {
        return getLong(context, key, -1);
    }

    /**
     * get long preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a long
     */
    public static long getLong(Context context, String key, long defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getLong(key, defaultValue);
    }

    /**
     * put float preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putFloat(Context context, String key, float value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        return editor.commit();
    }

    /**
     * get float preferences
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or -1. Throws ClassCastException if there is a preference with this
     * name that is not a float
     * @see #getFloat(Context, String, float)
     */
    public static float getFloat(Context context, String key) {
        return getFloat(context, key, -1);
    }

    /**
     * get float preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a float
     */
    public static float getFloat(Context context, String key, float defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getFloat(key, defaultValue);
    }

    /**
     * put boolean preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     * @return True if the new values were successfully written to persistent storage.
     */
    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     * get boolean preferences, default is false
     *
     * @param context
     * @param key     The name of the preference to retrieve
     * @return The preference value if it exists, or false. Throws ClassCastException if there is a preference with this
     * name that is not a boolean
     * @see #getBoolean(Context, String, boolean)
     */
    public static boolean getBoolean(Context context, String key) {
        return getBoolean(context, key, false);
    }

    /**
     * get boolean preferences
     *
     * @param context
     * @param key          The name of the preference to retrieve
     * @param defaultValue Value to return if this preference does not exist
     * @return The preference value if it exists, or defValue. Throws ClassCastException if there is a preference with
     * this name that is not a boolean
     */
    public static boolean getBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return settings.getBoolean(key, defaultValue);
    }
    //=============================================================================================

    /**
     * 把hashmap转换成字符串，存入sharedPreference
     *
     * @param context
     * @param key
     * @param map
     * @return
     */
    public static boolean putHashMap(Context context, String key, HashMap<String, String> map) {
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            String value = prcessHashMap(map);
            editor.putString(key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return editor.commit();
    }

    /**
     * 把hashmap通过byteArrayOutPutStream输出为字节数组，再解析成base64编码的字符串
     */
    private static String prcessHashMap(HashMap<String, String> map) throws IOException {
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArray);
        outputStream.writeObject(map);
        String result = Base64.encodeToString(byteArray.toByteArray(), Base64.DEFAULT);
        outputStream.close();
        return result;
    }

    public static HashMap<String, String> getHashMap(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String orign = sp.getString(key, null);
        try {
            return unprcessHashMap(orign);
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param orign
     * @return
     * @throws StreamCorruptedException 从对象流中读取的控制信息违反内部一致性检查时抛出。
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static HashMap<String, String> unprcessHashMap(String orign) throws StreamCorruptedException, ClassNotFoundException, IOException {
        byte[] origan = Base64.decode(orign, Base64.DEFAULT);
        ByteArrayInputStream inArray = new ByteArrayInputStream(origan);
        ObjectInputStream inputStream = new ObjectInputStream(inArray);
        HashMap<String, String> result = (HashMap<String, String>) inputStream.readObject();
        inputStream.close();
        return result;
    }

    /**
     * 保存List<String>
     *
     * @param key
     * @param values
     */
    public static boolean putStringList(Context context, String key, List<String> values) {
        SharedPreferences.Editor edit = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        edit.putInt(key, values.size());
        for (int i = 0; i < values.size(); i++) {
            edit.putString(key + i, values.get(i));
        }
        return edit.commit();
    }

    /**
     * 获取List<String>
     *
     * @param key
     * @return
     */
    public static List<String> getStringList(Context context, String key) {
        List<String> values = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        int listSize = sp.getInt(key, 0);
        for (int i = 0; i < listSize; i++) {
            values.add(sp.getString(key + i, null));
        }
        return values;
    }

    /**
     * 保存List<Integer>
     *
     * @param key
     * @param values
     */
    public static boolean putIntList(Context context, String key, List<Integer> values) {
        SharedPreferences.Editor edit = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        edit.putInt(key, values.size());
        for (int i = 0; i < values.size(); i++) {
            edit.putInt(key + i, values.get(i));
        }
        return edit.commit();
    }

    /**
     * 获取List<Integer>
     *
     * @param key
     * @return
     */
    public static List<Integer> getIntList(Context context, String key) {
        List<Integer> values = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        int listSize = sp.getInt(key, 0);
        for (int i = 0; i < listSize; i++) {
            values.add(sp.getInt(key + i, 0));
        }
        return values;
    }

    /**
     * 保存List<Long>
     *
     * @param key
     * @param values
     */
    public static boolean putLongList(Context context, String key, List<Long> values) {
        SharedPreferences.Editor edit = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        edit.putInt(key, values.size());
        for (int i = 0; i < values.size(); i++) {
            edit.putLong(key + i, values.get(i));
        }
        return edit.commit();
    }

    /**
     * 获取List<Long>
     *
     * @param key
     * @return
     */
    public static List<Long> getLongList(Context context, String key) {
        List<Long> values = new ArrayList<>();
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        int listSize = sp.getInt(key, 0);
        for (int i = 0; i < listSize; i++) {
            values.add(sp.getLong(key + i, 0));
        }
        return values;
    }

    /**
     * 保存List<Object>
     *
     * @param context
     * @param key
     * @param datalist
     */
    public <T> void putListBean(Context context, String key, List<T> datalist) {
        SharedPreferences.Editor edit = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        if (null == datalist || datalist.size() <= 0) {
            return;
        }
        Gson gson = new Gson();
        //转换成json数据，再保存
        String strJson = gson.toJson(datalist);
        edit.putString(key, strJson);
        edit.commit();
    }

    /**
     * 获取List<Object>
     *
     * @param context
     * @param key
     * @return listBean
     */
    public <T> List<T> getListBean(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        List<T> dataList = new ArrayList<T>();
        String strJson = sp.getString(key, null);
        if (null == strJson) {
            return dataList;
        }
        Gson gson = new Gson();
        dataList = gson.fromJson(strJson, new TypeToken<List<T>>() {
        }.getType());
        return dataList;
    }

    /**
     * 存放实体类以及任意类型
     *
     * @param context 上下文对象
     * @param key
     * @param obj
     */
    public static void saveBean(Context context, String key,
                                Object obj) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String objString = gson.toJson(obj);
        editor.putString(key, objString).apply();
    }

    /**
     * @param context
     * @param key
     * @param clazz   这里传入一个类就是我们所需要的实体类(obj)
     * @return 返回我们封装好的该实体类(obj)
     */
    public static <T> T getBean(Context context, String key,
                                Class<T> clazz) {
        //从sharedPreference中获取key的preference
        String objString = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).getString(key, "");
        //gson
        Gson gson = new Gson();
        return gson.fromJson(objString, clazz);
    }
}

