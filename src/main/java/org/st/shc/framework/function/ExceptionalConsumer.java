package org.st.shc.framework.function;

import java.util.Objects;

/**
 * 带有异常 的 Consumer
 *
 * @author abomb4 2022-06-25
 */
@FunctionalInterface
public interface ExceptionalConsumer<T> {

    /**
     * Performs this operation on the given argument.
     *
     * @param t the input argument
     * @throws Exception Any exception duration running
     */
    void accept(T t) throws Exception;

    /**
     * Returns a composed {@code Consumer} that performs, in sequence, this
     * operation followed by the {@code after} operation. If performing either
     * operation throws an exception, it is relayed to the caller of the
     * composed operation.  If performing this operation throws an exception,
     * the {@code after} operation will not be performed.
     *
     * @param after the operation to perform after this operation
     * @return a composed {@code Consumer} that performs in sequence this
     * operation followed by the {@code after} operation
     * @throws NullPointerException if {@code after} is null
     */
    default ExceptionalConsumer<T> andThen(ExceptionalConsumer<? super T> after) {
        Objects.requireNonNull(after);
        return (T t) -> {
            accept(t);
            after.accept(t);
        };
    }
}
