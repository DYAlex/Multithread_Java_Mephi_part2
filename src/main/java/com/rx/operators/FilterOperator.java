package com.rx.operators;

import com.rx.core.RxObservable;
import com.rx.core.RxObserver;
import com.rx.core.RxDisposable;

import java.util.function.Predicate;

/**
 * The filter operator: skips only those elements that satisfy the predicate.
 */
public class FilterOperator {
    public static <T> RxObservable<T> apply(RxObservable<T> source, Predicate<? super T> predicate) {
        return RxObservable.create(observer -> {
            RxDisposable disposable = source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    if (predicate.test(item)) {
                        observer.onNext(item);
                    }
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

