package com.unclezs.utils.thead;

import cn.hutool.core.thread.ThreadUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread pool for workers.<br><br>
 * Use {@link ExecutorService} as inner implement. <br><br>
 * New feature: <br><br>
 * 1. Block when thread pool is full to avoid poll many urls without progress. <br><br>
 * 2. Count of thread alive for monitor.
 *
 * @author code4crafer@gmail.com
 * @since 0.5.0
 */
public class CountableThreadPool {

    private ExecutorService executorService;
    private int threadNum;

    private AtomicInteger threadAlive = new AtomicInteger();

    private ReentrantLock reentrantLock = new ReentrantLock();

    private Condition condition = reentrantLock.newCondition();

    public CountableThreadPool(int threadNum) {
        this.threadNum = threadNum;
        this.executorService = Executors.newFixedThreadPool(threadNum);
    }

    public CountableThreadPool(int threadNum, ExecutorService executorService) {
        this.threadNum = threadNum;
        this.executorService = executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public int getThreadAlive() {
        return threadAlive.get();
    }

    public int getThreadNum() {
        return threadNum;
    }


    public void execute(final Runnable runnable) {
        if (threadAlive.get() >= threadNum) {
            reentrantLock.lock();
            try {
                while (threadAlive.get() >= threadNum) {
                    try {
                        condition.await();
                    } catch (InterruptedException ignored) {
                    }
                }
            } finally {
                reentrantLock.unlock();
            }
        }
        threadAlive.incrementAndGet();
        executorService.execute(() -> {
            try {
                runnable.run();
            } finally {
                reentrantLock.lock();
                try {
                    threadAlive.decrementAndGet();
                    condition.signal();
                } finally {
                    reentrantLock.unlock();
                }
            }
        });
    }

    public boolean isShutdown() {
        return executorService.isShutdown();
    }

    public boolean isTerminated() {
        return this.executorService.isTerminated();
    }

    public void shutdown() {
        executorService.shutdown();
    }

    /**
     * 阻塞线程直到所有线程完成
     */
    public void waitToOver() {
        while (getThreadAlive() != 0 && !isTerminated()) {
            ThreadUtil.sleep(200);
        }
    }
}
