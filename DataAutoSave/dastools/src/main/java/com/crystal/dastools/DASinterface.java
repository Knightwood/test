package com.crystal.dastools;

import android.os.Bundle;

/**
 * 创建者 kiylx
 * 创建时间 2020/6/23 23:19
 */
public interface DASinterface<T> {
    void RestoreData(T target, Bundle dataStore);

    void SaveData(T target, Bundle dataStore);
}
