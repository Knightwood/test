package com.example.kiylx.ti.DownloadCore;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskPool {
    private static final String TAG = "TaskPool";

    //线程池
    private static volatile TaskPool sTaskPool;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int THREAD_SIZE = Math.max(3, Math.min(CPU_COUNT - 1, 5));
    //核心线程数
    private static final int CORE_POOL_SIZE = THREAD_SIZE;
    //线程池
    private ExecutorService mExecutorService;
    //private final Deque<CustomDownloadTask> readyTasks = new ArrayDeque<>();
    private final Deque<CustomDownloadTask> runningTasks = new ArrayDeque<>();
    //private final Deque<CustomDownloadTask> stopTasks = new ArrayDeque<>();
    private CustomDownloadTask downloadTask;

    private TaskPool() {
    }

    public static TaskPool getInstance() {
        if (sTaskPool == null) {
            synchronized (TaskPool.class) {
                if (sTaskPool == null) {
                    sTaskPool = new TaskPool();
                }
            }
        }
        return sTaskPool;
    }

    public synchronized ExecutorService getExecutorService() {
        if (mExecutorService == null) {
            mExecutorService = new ThreadPoolExecutor(
                    CORE_POOL_SIZE,
                    Integer.MAX_VALUE,
                    60,
                    TimeUnit.SECONDS,
                    new SynchronousQueue<Runnable>());
        }
        return mExecutorService;
    }


}
