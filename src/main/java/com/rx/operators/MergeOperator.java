package com.rx.operators;

import com.rx.core.RxCompositeDisposable;
import com.rx.core.RxDisposable;
import com.rx.core.RxObservable;
import com.rx.core.RxObserver;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Operator merge: merges several Observables into a single stream in parallel.
 */
public class MergeOperator {

    /**
     * @param sources array of source Observable
     * @param <T>     type of elements
     * @return new RxObservable<T> that emits all sources elements
     */
    @SafeVarargs
    public static <T> RxObservable<T> apply(RxObservable<? extends T>... sources) {
        return RxObservable.create(observer -> {
            RxCompositeDisposable composite = new RxCompositeDisposable();
            AtomicInteger remaining = new AtomicInteger(sources.length);
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            for (RxObservable<? extends T> src : sources) {
                RxDisposable disposable = src.subscribe(new RxObserver<T>() {
                    @Override
                    public void onNext(T item) {
                        observer.onNext(item);
                    }

                    @Override
                    public void onError(Throwable t) {
                        errors.add(t);
                        completeIfDone();
                    }

                    @Override
                    public void onComplete() {
                        completeIfDone();
                    }

                    private void completeIfDone() {
                        if (remaining.decrementAndGet() == 0) {
                            Throwable err = errors.poll();
                            if (err != null) {
                                observer.onError(err);
                            } else {
                                observer.onComplete();
                            }
                            composite.dispose();
                        }
                    }
                });
                composite.add(disposable);
            }
        });
    }
}
