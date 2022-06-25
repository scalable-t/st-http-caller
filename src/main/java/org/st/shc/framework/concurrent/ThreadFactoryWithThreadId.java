package org.st.shc.framework.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可根据线程编号命名的线程工厂，适用于全局只有一个同类线程池的场景，应用比较广泛。
 * <p>
 * 正确用法 1 ： 使用匿名类：
 * <p>
 * {@code final ThreadFactory tf = new ThreadFactoryWithThreadId(tid -> "my-task-" + tid) {}}
 * <p>
 * 正确用法 2 ： 继承：
 * <p><pre>
 * class MyThreadFactory extends ThreadFactoryWithThreadId {
 *     public MyThreadFactory() {
 *         super(tid -&gt; "my-task-" + tid);
 *     }
 * }
 * // ...
 * final ThreadFactory tf = new MyThreadFactory();
 * </pre>
 * {@code }
 *
 * @author yangrl14628
 * @date 2022-05-18 11:46:33
 * <p>
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 */
public abstract class ThreadFactoryWithThreadId implements ThreadFactory {
    /** 线程 id */
    final AtomicInteger threadIdGen = new AtomicInteger(0);
    /** 线程名组装函数 */
    final ThreadNamingFunction func;

    /**
     * 根据线程名组装函数构造
     *
     * @param func 线程名组装函数
     */
    public ThreadFactoryWithThreadId(ThreadNamingFunction func) {
        this.func = func;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, makeName(this.threadIdGen.incrementAndGet()));
    }

    /**
     * 拼装线程名
     *
     * @param threadId 线程 id
     * @return 线程名
     */
    protected String makeName(int threadId) {
        return func.makeName(threadId);
    }

    /** 线程名组装函数 */
    @FunctionalInterface
    public interface ThreadNamingFunction {

        /**
         * 线程名组装
         *
         * @param threadId 线程 id
         * @return 线程名
         */
        String makeName(int threadId);
    }
}
