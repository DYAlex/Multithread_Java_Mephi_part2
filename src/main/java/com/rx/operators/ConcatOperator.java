package com.rx.operators;

import com.rx.core.RxObservable;
import com.rx.core.RxObserver;

/**
 * Concat operator: sequential concatenation of two Observables.
 */
public class ConcatOperator {
    public static <T> RxObservable<T> apply(RxObservable<? extends T> first, RxObservable<? extends T> second) {
        return RxObservable.create(observer -> {
            first.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    observer.onNext(item);
                }

                @Override
                public void onError(Throwable t) {
                    observer.onError(t);
                }

                @Override
                public void onComplete() {
                    second.subscribe(observer);
                }
            });
        });
    }
}

