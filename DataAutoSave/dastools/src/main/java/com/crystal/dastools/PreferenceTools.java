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
import java.util.Set;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/26 16:11
 */
public class PreferenceTools {

    private static String PREFERENCE_NAME = "conf_DATASAVEAUTO_veR1";
    private static final List<String> SUPPORTED_ARRAY = Arrays.asList("boolean[]", "int[]", "double[]", "float[]", "long[]", "byte[]", "char[]", "short[]");
    private static final List<String> SUPPORTED_Num = Arrays.asList("int", "long", "float", "double", "byte", "short");
    public static HashMap<String, String> Method_SUFFIX = new HashMap<>();

    static {
        if (Method_SUFFIX.isEmpty()) {
            for (String m : SUPPORTED_ARRAY) {
                Method_SUFFIX.put(m, "Arrays");
            }
            for (String num : SUPPORTED_Num) {
                Method_SUFFIX.put(num, "Num");
            }
            Method_SUFFIX.put("java.lang.String[]", "StringArray");
            Method_SUFFIX.put("boolean", "Boolean");
            Method_SUFFIX.put("char", "String");
            Method_SUFFIX.put("java.lang.String", "String");
            Method_SUFFIX.put("java.util.Set<java.lang.String>", "StringSet");
            Method_SUFFIX.put("java.util.HashMap<java.lang.String,java.lang.String>", "HashMap");
            Method_SUFFIX.put("java.util.List<java.lang.Integer>", "IntList");
            Method_SUFFIX.put("java.util.List<java.lang.Long>", "LongList");
            Method_SUFFIX.put("java.util.List<java.lang.String>", "StringList");
            //Method_SUFFIX.put("java.lang.Object", "Bean");
            //beanList
            //bean
        }
    }


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
     *                <p>  "boolean[]","int[]", "double[]", "float[]", "long[]", "byte[]", "char[]", "short[]",
     *                </p>
     */
    public static <T> void putArrays(Context context, String key, T[] arrsys) {
        if (arrsys==null||arrsys.length<=0){
            return;
        }
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
     *                <p>  "boolean[]","int[]", "double[]", "float[]", "long[]", "byte[]", "char[]", "short[]",
     * @return
     */
    public static String[] getArrays(Context context, String key) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        int length = settings.getInt(key, 0);
        String[] arrays = new String[length + 3];
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                arrays[i] = settings.getString(key + i, "");
            }
        }
        return arrays;
    }

    /**
     * @param context
     * @param key
     * @param value   T类型的数据
     *                "int", "long", "float", "double", "byte", "short",
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
     * "double", "byte",  "short",
     * 存储时使用了string，取出来时也返回string类型
     */
    public static String getNum(Context context, String key) {
        return getString(context, key);
    }

    public static void putStringSet(Context context, String key, Set<String> value) {
        if (value==null||value.isEmpty()){
            return;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key, value);
        editor.apply();
    }

    public static Set<String> getStringSet(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);

        return sharedPreferences.getStringSet(key, null);
    }


    /**
     * @param context
     * @param key
     * @param arrsys  要保存的数组
     *                "java.lang.String[]"
     */
    public static void putStringArray(Context context, String key, String[] arrsys) {
        putStringList(context, key, Arrays.asList(arrsys));
    }

    public static String[] getStringArray(Context context, String key) {
        List<String> list = getStringList(context, key);
        return list.toArray(new String[list.size()]);
    }

    enum StrCastBoolean {
        TRUE(true, "true"), FALSE(false, "false");
        boolean b;
        String s;

        StrCastBoolean(boolean b, String s) {
            this.b = b;
            this.s = s;
        }

        String getString(boolean b) {
            for (StrCastBoolean s : StrCastBoolean.values()) {
                if (s.b == b) {
                    return s.s;
                }
            }
            return null;
        }

        boolean getBoolean(String s) {
            for (StrCastBoolean strCastBoolean : StrCastBoolean.values()) {
                if (strCastBoolean.s.equals(s)) {
                    return strCastBoolean.b;
                }
            }
            return false;
        }
    }


    /**
     * put string preferences
     *
     * @param context
     * @param key     The name of the preference to modify
     * @param value   The new value for the preference
     */
    public static void putString(Context context, String key, String value) {
        if (value==null){
            return;
        }
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(key, value);
        editor.apply();
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
     */
    public static void putInt(Context context, String key, int value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(key, value);
        editor.apply();
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
     */
    public static void putLong(Context context, String key, long value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(key, value);
        editor.apply();
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
    public static void putFloat(Context context, String key, float value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putFloat(key, value);
        editor.apply();
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
    public static void putBoolean(Context context, String key, boolean value) {
        SharedPreferences settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(key, value);
        editor.apply();
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
    public static void putHashMap(Context context, String key, HashMap<String, String> map) {
        if (map==null||map.isEmpty()){
            return;
        }
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        try {
            String value = prcessHashMap(map);
            editor.putString(key, value);
        } catch (IOException e) {
            e.printStackTrace();
        }

        editor.apply();
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
    public static void putStringList(Context context, String key, List<String> values){
        if (values==null||values.isEmpty()){
            return;
        }
        SharedPreferences.Editor edit = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        edit.putInt(key, values.size());
        for (int i = 0; i < values.size(); i++) {
            edit.putString(key + i, values.get(i));
        }
        edit.apply();
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
    public static void putIntList(Context context, String key, List<Integer> values){
        if (values==null||values.isEmpty()){
            return;
        }
        SharedPreferences.Editor edit = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        edit.putInt(key, values.size());
        for (int i = 0; i < values.size(); i++) {
            edit.putInt(key + i, values.get(i));
        }
        edit.apply();
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
    public static void putLongList(Context context, String key, List<Long> values) {
        if (values==null||values.isEmpty()){
            return;
        }
        SharedPreferences.Editor edit = context.getSharedPreferences(
                PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        edit.putInt(key, values.size());
        for (int i = 0; i < values.size(); i++) {
            edit.putLong(key + i, values.get(i));
        }
        edit.apply();
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
        edit.apply();
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
    public static void putBean(Context context, String key,
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

