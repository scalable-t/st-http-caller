package org.st.shc.framework.concurrent;

import java.util.Objects;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 针对 {@link java.util.concurrent.ScheduledThreadPoolExecutor} 使用的装饰器
 *
 * @author yangrl14628
 * @date 2022-05-18 09:42:41
 * <p>
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 */
class RunnableScheduledFutureDecorate<V> implements RunnableScheduledFuture<V> {

    /** 任务 */
    private final RunnableScheduledFuture<V> delegate;
    /** 被装饰过的任务 */
    private final Runnable run;

    /**
     * 根据任务构造
     *
     * @param decorator 任务装饰器
     * @param task      任务
     */
    RunnableScheduledFutureDecorate(ConcurrentTaskDecorator decorator, RunnableScheduledFuture<V> task) {
        Objects.requireNonNull(task);
        this.delegate = task;
        this.run = decorator.decorate((Runnable) task);
    }

    @Override
    public boolean isPeriodic() {
        return delegate.isPeriodic();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return delegate.getDelay(unit);
    }

    @Override
    public int compareTo(Delayed o) {
        return delegate.compareTo(o);
    }

    @Override
    public void run() {
        run.run();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return delegate.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return delegate.isCancelled();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return delegate.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return delegate.get(timeout, unit);
    }
}
