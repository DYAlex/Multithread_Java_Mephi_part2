package com.rx.operators;

import com.rx.core.RxObservable;
import com.rx.core.RxObserver;
import com.rx.core.RxDisposable;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

/**
 * Operator reduce: accumulation of elements into one total value.
 */
public class ReduceOperator {
    public static <T> RxObservable<T> apply(RxObservable<T> source, BiFunction<? super T, ? super T, ? extends T> accumulator) {
        return RxObservable.create(observer -> {
            AtomicReference<T> acc = new AtomicReference<>();
            RxDisposable disposable = source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    if (acc.get() == null) {
                        acc.set(item);
                    } else {
                        acc.set(accumulator.apply(acc.get(), item));
                    }
                }

                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                }

                @Override
                public void onComplete() {
                    T result = acc.get();
                    if (result != null) {
                        observer.onNext(result);
                    }
                    observer.onComplete();
                }
            });
        });
    }
}

