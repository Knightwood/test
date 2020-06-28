package com.crystal.dastools;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/27 18:21
 */
public interface DataSaveInterface<T> {
    void RestoreData(T context);

    void SaveData(T context);
}
