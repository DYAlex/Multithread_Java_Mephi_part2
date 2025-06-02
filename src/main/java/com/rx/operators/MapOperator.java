package com.rx.operators;

import com.rx.core.RxObservable;
import com.rx.core.RxObserver;
import com.rx.core.RxDisposable;

import java.util.function.Function;

/**
 * The map operator: applies a function to each element of the flow.
 */
public class MapOperator {
    public static <T, R> RxObservable<R> apply(RxObservable<T> source, Function<? super T, ? extends R> mapper) {
        return RxObservable.create(observer -> {
            RxDisposable disposable = source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    observer.onNext(mapper.apply(item));
                }

                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                }

                @Override
                public void onComplete() {
                    observer.onComplete();
                }
            });
        });
    }
}

