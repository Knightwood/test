package com.example.kiylx.ti.tool;

import java.util.List;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/10 22:49
 * packageName：com.example.kiylx.ti.tool
 * 描述：
 */
public class KeyValue<T,K> {

        public List<T> one;
        public List<K> two;

        public KeyValue(List<T> one, List<K> two) {
            this.one = one;
            this.two = two;
        }

}
