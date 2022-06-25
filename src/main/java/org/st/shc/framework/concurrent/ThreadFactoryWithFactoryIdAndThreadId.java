package org.st.shc.framework.concurrent;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 可根据工厂编号与线程编号命名的线程工厂，适用于会多次创建同类线程池的场景。
 * <p>
 * 正确用法 1 ： 使用匿名类：
 * <p>
 * {@code final ThreadFactory tf = new ThreadFactoryWithFactoryIdAndThreadId((fid, tid) -> "my-pool-" + fid +
 * "-thread-" + tid) {}}
 * <p>
 * 正确用法 2 ： 继承：
 * <p><pre>
 * class MyThreadFactory extends ThreadFactoryWithFactoryIdAndThreadId {
 *     public MyThreadFactory() {
 *         super((fid, tid) -&gt; "my-pool-" + fid + "-thread-" + tid);
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
public abstract class ThreadFactoryWithFactoryIdAndThreadId implements ThreadFactory {
    /** 静态的工厂 id */
    static final AtomicInteger FACTORY_ID_GEN = new AtomicInteger(0);
    /** 工厂内线程 id */
    final AtomicInteger threadIdGen = new AtomicInteger(0);
    /** 工厂 id */
    final int factoryId = FACTORY_ID_GEN.incrementAndGet();
    /** 线程名组装函数 */
    final ThreadNamingFunction func;

    /**
     * 根据线程名组装函数构造
     *
     * @param func 线程名组装函数
     */
    public ThreadFactoryWithFactoryIdAndThreadId(ThreadNamingFunction func) {
        this.func = func;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, makeName(this.factoryId, this.threadIdGen.incrementAndGet()));
    }

    /**
     * 拼装线程名
     *
     * @param factoryId 工厂 id
     * @param threadId  线程 id
     * @return 线程名
     */
    protected String makeName(int factoryId, int threadId) {
        return func.makeName(factoryId, threadId);
    }

    /** 线程名组装函数 */
    @FunctionalInterface
    public interface ThreadNamingFunction {

        /**
         * 线程名组装
         *
         * @param factoryId 工厂 id
         * @param threadId  线程 id
         * @return 线程名
         */
        String makeName(int factoryId, int threadId);
    }
}
