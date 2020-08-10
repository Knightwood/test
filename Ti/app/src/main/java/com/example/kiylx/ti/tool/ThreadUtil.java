package com.example.kiylx.ti.tool;

import java.util.concurrent.Callable;

/**
 * 创建者 kiylx
 * 创建时间 2020/8/9 14:30
 * packageName：com.example.kiylx.ti.tool
 * 描述：
 */
public class ThreadUtil {

    public interface WorkMethod{
        void method(Object[] objs);
    }

    public static class MethodThread implements Runnable{
        WorkMethod method;
        Object[] objs;

        public MethodThread(WorkMethod method ,Object...objs) {
            this.method=method;
            this.objs=objs;
        }

        @Override
        public void run() {
            method.method(objs);
        }
    }

    public interface WorkCallMethod<T>{
        T method(Object[] objs);
    }

    public static class MethodCallThread<T> implements Callable<T> {
        WorkCallMethod method;
        Object[] objs;

        public MethodCallThread(WorkCallMethod method , Object...objs) {
            this.method=method;
            this.objs=objs;
        }

        @Override
        public T call() throws Exception {
            return (T) method.method(objs);
        }
    }
}
