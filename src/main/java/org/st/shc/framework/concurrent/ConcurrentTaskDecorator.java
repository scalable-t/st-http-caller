package org.st.shc.framework.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.RunnableScheduledFuture;

/**
 * 综合型裱框师，可对各类标准异步任务进行裱框
 *
 * @author yangrl14628
 * @date 2022-05-18 09:37:11
 * <p>
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 */
public interface ConcurrentTaskDecorator {

    /**
     * 给一个可以运行的东西装上那好看的画框
     *
     * @param runnable Runnable
     * @return Runnable
     */
    Runnable decorate(Runnable runnable);

    /**
     * 给一个可以运行的东西装上那好看的画框
     *
     * @param callable 可调用
     * @return Runnable
     */
    <T> Callable<T> decorate(Callable<T> callable);

    /**
     * 针对 {@link java.util.concurrent.ScheduledThreadPoolExecutor} 使用的装饰方法
     *
     * @param task 异步任务
     * @param <V>  任意类型
     * @return 被装饰的任务
     */
    default <V> RunnableScheduledFuture<V> decorate(RunnableScheduledFuture<V> task) {
        return new RunnableScheduledFutureDecorate<>(this, task);
    }
}
