package org.st.shc.framework.concurrent;

import java.util.Collection;
import java.util.concurrent.Callable;

/**
 * 集合一堆裱框师的裱框师
 *
 * @author yangrl14628
 * @date 2022-05-18 09:37:11
 * <p>
 * Copyright © 2022 Hundsun Technologies Inc. All Rights Reserved
 */
public class ConcurrentTaskDecorators implements ConcurrentTaskDecorator {

    /** 集合 */
    private final Collection<ConcurrentTaskDecorator> decorators;

    /**
     * 完整构造
     *
     * @param decorators 装饰器
     */
    public ConcurrentTaskDecorators(Collection<ConcurrentTaskDecorator> decorators) {
        this.decorators = decorators;
    }

    @Override
    public Runnable decorate(Runnable runnable) {
        if (runnable == null) {
            return null;
        }

        for (ConcurrentTaskDecorator decorator : decorators) {
            runnable = decorator.decorate(runnable);
        }

        return runnable;
    }

    @Override
    public <T> Callable<T> decorate(Callable<T> callable) {
        if (callable == null) {
            return null;
        }

        for (ConcurrentTaskDecorator decorator : decorators) {
            callable = decorator.decorate(callable);
        }

        return callable;
    }
}
