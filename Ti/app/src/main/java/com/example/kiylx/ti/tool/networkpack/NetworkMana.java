package com.example.kiylx.ti.tool.networkpack;

import java.util.HashMap;
import java.util.Map;

/**
 * 创建者 kiylx
 * 创建时间 2020/7/9 23:42
 */
public enum NetworkMana {
    INSTANCE;
    private static NetEntity[] netWorkState;//网络状态

    static {
        netWorkState = new NetEntity[2];
    }

    public void clear(NetEntity... entities) {

    }

    public void update(NetEntity... entities) {

    }

    public Boolean get(NetState key) {
        return false;
    }

    public void destroy() {

    }


    public static class NetEntity implements Map.Entry<NetState, Boolean> {
        private NetState state;
        private Boolean value;

        public NetEntity(NetState state, Boolean value) {
            this.state = state;
            this.value = value;
        }

        @Override
        public NetState getKey() {
            return state;
        }

        @Override
        public Boolean getValue() {
            return value;
        }

        @Override
        public Boolean setValue(Boolean value) {
            this.value=value;
            return this.value;
        }

    }


}
