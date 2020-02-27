package com.example.kiylx.ti.livedata;

/**
 * @创建者 kiylx
 * @创建时间 2020/2/27 15:46
 */
public class DefaultValue {
    private String user_agent;

    public DefaultValue(String s){
        this.user_agent=s;
    }


    public String getUser_agent() {
        return user_agent;
    }

    public void setUser_agent(String user_agent) {
        this.user_agent = user_agent;
    }
}
