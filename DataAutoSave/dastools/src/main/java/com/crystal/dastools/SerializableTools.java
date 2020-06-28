package com.crystal.dastools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/28 12:42
 */
public class SerializableTools {

    /**
     * @param object 要存储的对象
     * @param path 存储文件的路径
     * @param <T> object
     *           存储对象到特定文件
     */
    public static <T extends Object> void writeObj(T object, String path) {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(path));
            objectOutputStream.writeObject(object);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static <T extends Object> T parseObj(String path) {
        T obj = null;
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path));
            try {
                obj = (T) inputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

}
