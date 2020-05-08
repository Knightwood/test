package com.example.kiylx.ti.tool;

/**
 * 创建者 kiylx
 * 创建时间 2020/5/6 11:40
 */
public class GlobalData {
    private static GlobalData mGlobal;



    public static GlobalData getInstance(){
        if (mGlobal==null){
            synchronized (GlobalData.class){
                if (mGlobal==null){
                    mGlobal=new GlobalData();
                }
            }
        }
        return mGlobal;
    }


}
