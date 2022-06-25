package org.st.shc.framework.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池构造器，支持装饰器模式，每次创建任务时在 runnable 或 callable 外面套一层东西
 *
 * @author yangrl14628
 * @date 2022-05-18 10:03:04
 * <p>
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 */
public class ExecutorsBuilder {

    /**
     * 构造定时线程池
     *
     * @return 构造器
     */
    public static ScheduledThreadPoolExecutorBuilder newScheduledBuilder() {
        return new ScheduledThreadPoolExecutorBuilder();
    }

    /**
     * 构造线程池
     *
     * @return 构造器
     */
    public static ThreadPoolExecutorBuilder newBuilder() {
        return new ThreadPoolExecutorBuilder();
    }

    /** 构造定时线程池 */
    public static class ScheduledThreadPoolExecutorBuilder {

        /** 核心线程数 */
        private int corePoolSize = 16;
        /** 线程工厂，用于命名 */
        private ThreadFactory threadFactory;
        /** 拒绝策略 */
        private RejectedExecutionHandler rejectExecutionHandler;
        /** 任务装饰器 */
        private ConcurrentTaskDecorator decorator;

        /**
         * 构建线程池
         *
         * @return 线程池
         */
        public ScheduledThreadPoolExecutor build() {
            if (threadFactory != null && rejectExecutionHandler != null) {
                if (decorator != null) {
                    return new ScheduledThreadPoolExecutorDecorated(corePoolSize, threadFactory,
                            rejectExecutionHandler, decorator);
                } else {
                    return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory, rejectExecutionHandler);
                }
            }

            if (threadFactory != null) {
                if (decorator != null) {
                    return new ScheduledThreadPoolExecutorDecorated(corePoolSize, threadFactory, decorator);
                } else {
                    return new ScheduledThreadPoolExecutor(corePoolSize, threadFactory);
                }
            }

            if (rejectExecutionHandler != null) {
                if (decorator != null) {
                    return new ScheduledThreadPoolExecutorDecorated(corePoolSize, rejectExecutionHandler, decorator);
                } else {
                    return new ScheduledThreadPoolExecutor(corePoolSize, rejectExecutionHandler);
                }
            }

            if (decorator != null) {
                return new ScheduledThreadPoolExecutorDecorated(corePoolSize, decorator);
            } else {
                return new ScheduledThreadPoolExecutor(corePoolSize);
            }
        }

        /**
         * 设置 核心线程数
         *
         * @param corePoolSize 核心线程数
         * @return this
         */
        public ScheduledThreadPoolExecutorBuilder setCorePoolSize(int corePoolSize) {
            if (corePoolSize < 0) {
                throw new IllegalArgumentException("corePoolSize cannot lesser than 0");
            }
            this.corePoolSize = corePoolSize;
            return this;
        }

        /**
         * 设置 线程工厂，用于命名
         *
         * @param threadFactory 线程工厂，用于命名
         * @return this
         */
        public ScheduledThreadPoolExecutorBuilder setThreadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        /**
         * 设置 拒绝策略
         *
         * @param rejectExecutionHandler 拒绝策略
         * @return this
         */
        public ScheduledThreadPoolExecutorBuilder setRejectExecutionHandler(
                RejectedExecutionHandler rejectExecutionHandler) {
            this.rejectExecutionHandler = rejectExecutionHandler;
            return this;
        }

        /**
         * 设置 任务装饰器
         *
         * @param decorator 任务装饰器
         * @return this
         */
        public ScheduledThreadPoolExecutorBuilder setDecorator(ConcurrentTaskDecorator decorator) {
            this.decorator = decorator;
            return this;
        }

        /** 带有装饰器的计划线程池 */
        private static class ScheduledThreadPoolExecutorDecorated extends ScheduledThreadPoolExecutor {

            /** 任务装饰器 */
            private ConcurrentTaskDecorator decorator;

            /**
             * 附类构造器
             *
             * @param corePoolSize 核心线程数
             * @param decorator    任务装饰器
             */
            public ScheduledThreadPoolExecutorDecorated(int corePoolSize, ConcurrentTaskDecorator decorator) {
                super(corePoolSize);
                this.decorator = decorator;
            }

            /**
             * 附类构造器
             *
             * @param corePoolSize  核心线程数
             * @param threadFactory 线程工厂，用于命名
             * @param decorator     任务装饰器
             */
            public ScheduledThreadPoolExecutorDecorated(int corePoolSize, ThreadFactory threadFactory,
                                                        ConcurrentTaskDecorator decorator) {
                super(corePoolSize, threadFactory);
                this.decorator = decorator;
            }

            /**
             * 附类构造器
             *
             * @param corePoolSize 核心线程数
             * @param handler      拒绝策略
             * @param decorator    任务装饰器
             */
            public ScheduledThreadPoolExecutorDecorated(int corePoolSize, RejectedExecutionHandler handler,
                                                        ConcurrentTaskDecorator decorator) {
                super(corePoolSize, handler);
                this.decorator = decorator;
            }

            /**
             * 附类构造器
             *
             * @param corePoolSize  核心线程数
             * @param threadFactory 线程工厂，用于命名
             * @param handler       拒绝策略
             * @param decorator     任务装饰器
             */
            public ScheduledThreadPoolExecutorDecorated(int corePoolSize, ThreadFactory threadFactory,
                                                        RejectedExecutionHandler handler,
                                                        ConcurrentTaskDecorator decorator) {
                super(corePoolSize, threadFactory, handler);
                this.decorator = decorator;
            }

            /**
             * 设置装饰器，一定要在启动前设置好
             *
             * @param decorator 装饰器
             */
            public void setDecorator(ConcurrentTaskDecorator decorator) {
                this.decorator = decorator;
            }

            @Override
            protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable,
                                                                  RunnableScheduledFuture<V> task) {
                return decorator.decorate(super.decorateTask(callable, task));
            }

            @Override
            protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
                return decorator.decorate(super.decorateTask(runnable, task));
            }

            @Override
            public void execute(Runnable command) {
                super.execute(decorator.decorate(command));
            }

            @Override
            protected <V> RunnableFuture<V> newTaskFor(Callable<V> c) {
                return super.newTaskFor(decorator.decorate(c));
            }

            @Override
            protected <V> RunnableFuture<V> newTaskFor(Runnable r, V v) {
                return super.newTaskFor(decorator.decorate(r), v);
            }
        }
    }

    /** 构造线程池 */
    public static class ThreadPoolExecutorBuilder {
        /** 核心线程数 */
        private int corePoolSize = 16;
        /** 最大线程数 */
        private int maximumPoolSize = 32;
        /** 非核心线程持续时间 */
        private long keepAliveTime = 1;
        /** 非核心线程持续时间单位 */
        private TimeUnit keepAliveTimeUnit = TimeUnit.MINUTES;
        /** 等待队列 */
        private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(4096);
        /** 线程工厂 */
        private ThreadFactory threadFactory;
        /** 拒绝策略 */
        private RejectedExecutionHandler rejectExecutionHandler;
        /** 任务装饰器 */
        private ConcurrentTaskDecorator decorator;

        /**
         * 构造线程池
         *
         * @return 线程池
         */
        public ThreadPoolExecutor build() {
            if (threadFactory != null && rejectExecutionHandler != null) {
                if (decorator != null) {
                    return new ThreadPoolExecutorDecorated(corePoolSize, maximumPoolSize, keepAliveTime,
                            keepAliveTimeUnit, workQueue, threadFactory, rejectExecutionHandler, decorator);
                } else {
                    return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, keepAliveTimeUnit,
                            workQueue, threadFactory, rejectExecutionHandler);
                }
            }

            if (threadFactory != null) {
                if (decorator != null) {
                    return new ThreadPoolExecutorDecorated(corePoolSize, maximumPoolSize, keepAliveTime,
                            keepAliveTimeUnit, workQueue, threadFactory, decorator);
                } else {
                    return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, keepAliveTimeUnit,
                            workQueue, threadFactory);
                }
            }

            if (rejectExecutionHandler != null) {
                if (decorator != null) {
                    return new ThreadPoolExecutorDecorated(corePoolSize, maximumPoolSize, keepAliveTime,
                            keepAliveTimeUnit, workQueue, rejectExecutionHandler, decorator);
                } else {
                    return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, keepAliveTimeUnit,
                            workQueue, rejectExecutionHandler);
                }
            }

            if (decorator != null) {
                return new ThreadPoolExecutorDecorated(corePoolSize, maximumPoolSize, keepAliveTime,
                        keepAliveTimeUnit, workQueue, decorator);
            } else {
                return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, keepAliveTimeUnit,
                        workQueue);
            }
        }

        /**
         * 设置核心线程数
         *
         * @param corePoolSize 核心线程数
         * @return this
         */
        public ThreadPoolExecutorBuilder setCorePoolSize(int corePoolSize) {
            if (corePoolSize < 0) {
                throw new IllegalArgumentException("corePoolSize cannot lesser than 0");
            }
            this.corePoolSize = corePoolSize;
            return this;
        }

        /**
         * 设置最大线程数
         *
         * @param maximumPoolSize 最大线程数
         * @return this
         */
        public ThreadPoolExecutorBuilder setMaximumPoolSize(int maximumPoolSize) {
            if (corePoolSize < 1) {
                throw new IllegalArgumentException("maximumPoolSize cannot lesser than 1");
            }
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        /**
         * 设置非核心线程持续时间
         *
         * @param keepAliveTime 非核心线程持续时间
         * @return this
         */
        public ThreadPoolExecutorBuilder setKeepAliveTime(long keepAliveTime) {
            if (keepAliveTime < 0) {
                throw new IllegalArgumentException("keepAliveTime cannot lesser than 0");
            }
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        /**
         * 设置非核心线程持续时间单位
         *
         * @param keepAliveTimeUnit 非核心线程持续时间单位
         * @return this
         */
        public ThreadPoolExecutorBuilder setKeepAliveTimeUnit(TimeUnit keepAliveTimeUnit) {
            if (keepAliveTimeUnit == null) {
                throw new IllegalArgumentException("unit cannot be null");
            }
            this.keepAliveTimeUnit = keepAliveTimeUnit;
            return this;
        }

        /**
         * 设置等待队列
         *
         * @param workQueue 等待队列
         * @return this
         */
        public ThreadPoolExecutorBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
            this.workQueue = workQueue;
            return this;
        }

        /**
         * 设置线程工厂
         *
         * @param threadFactory 线程工厂
         * @return this
         */
        public ThreadPoolExecutorBuilder setThreadFactory(ThreadFactory threadFactory) {
            this.threadFactory = threadFactory;
            return this;
        }

        /**
         * 设置拒绝策略
         *
         * @param rejectExecutionHandler 拒绝策略
         * @return this
         */
        public ThreadPoolExecutorBuilder setRejectExecutionHandler(RejectedExecutionHandler rejectExecutionHandler) {
            this.rejectExecutionHandler = rejectExecutionHandler;
            return this;
        }

        /**
         * 设置任务装饰器
         *
         * @param decorator 任务装饰器
         * @return this
         */
        public ThreadPoolExecutorBuilder setDecorator(ConcurrentTaskDecorator decorator) {
            this.decorator = decorator;
            return this;
        }

        /** 带装饰的线程池 */
        private static class ThreadPoolExecutorDecorated extends ThreadPoolExecutor {

            /** 任务装饰器 */
            private final ConcurrentTaskDecorator decorator;

            /**
             * 父类构造
             *
             * @param corePoolSize    核心线程数
             * @param maximumPoolSize 最大线程数
             * @param keepAliveTime   非核心线程持续时间
             * @param unit            非核心线程持续时间单位
             * @param workQueue       等待队列
             * @param decorator       任务装饰器
             */
            public ThreadPoolExecutorDecorated(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                               TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                               ConcurrentTaskDecorator decorator) {
                super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
                this.decorator = decorator;
            }

            /**
             * 父类构造
             *
             * @param corePoolSize    核心线程数
             * @param maximumPoolSize 最大线程数
             * @param keepAliveTime   非核心线程持续时间
             * @param unit            非核心线程持续时间单位
             * @param workQueue       等待队列
             * @param threadFactory   线程工厂
             * @param decorator       任务装饰器
             */
            public ThreadPoolExecutorDecorated(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                               TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                               ThreadFactory threadFactory, ConcurrentTaskDecorator decorator) {
                super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory);
                this.decorator = decorator;
            }

            /**
             * 父类构造
             *
             * @param corePoolSize    核心线程数
             * @param maximumPoolSize 最大线程数
             * @param keepAliveTime   非核心线程持续时间
             * @param unit            非核心线程持续时间单位
             * @param workQueue       等待队列
             * @param handler         拒绝策略
             * @param decorator       任务装饰器
             */
            public ThreadPoolExecutorDecorated(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                               TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                               RejectedExecutionHandler handler, ConcurrentTaskDecorator decorator) {
                super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler);
                this.decorator = decorator;
            }

            /**
             * 父类构造
             *
             * @param corePoolSize    核心线程数
             * @param maximumPoolSize 最大线程数
             * @param keepAliveTime   非核心线程持续时间
             * @param unit            非核心线程持续时间单位
             * @param workQueue       等待队列
             * @param threadFactory   线程工厂
             * @param handler         拒绝策略
             * @param decorator       任务装饰器
             */
            public ThreadPoolExecutorDecorated(int corePoolSize, int maximumPoolSize, long keepAliveTime,
                                               TimeUnit unit, BlockingQueue<Runnable> workQueue,
                                               ThreadFactory threadFactory, RejectedExecutionHandler handler,
                                               ConcurrentTaskDecorator decorator) {
                super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
                this.decorator = decorator;
            }

            @Override
            public void execute(Runnable command) {
                super.execute(decorator.decorate(command));
            }

            @Override
            protected <V> RunnableFuture<V> newTaskFor(Callable<V> c) {
                return super.newTaskFor(decorator.decorate(c));
            }

            @Override
            protected <V> RunnableFuture<V> newTaskFor(Runnable r, V v) {
                return super.newTaskFor(decorator.decorate(r), v);
            }
        }
    }

    /** no construct */
    private ExecutorsBuilder() {
    }
}
