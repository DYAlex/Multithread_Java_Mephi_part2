package com.rx.operators;

import com.rx.core.RxCompositeDisposable;
import com.rx.core.RxDisposable;
import com.rx.core.RxObservable;
import com.rx.core.RxObserver;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * The flatMap operator: creates a new Observable for each element of the source stream
 * and "flattens" its elements into a single resulting stream.
 */
public class FlatMapOperator {

    /**
     * @param source source Observable
     * @param mapper a function that generates a nested Observable for each element
     * @param <T>    type of source elements
     * @param <R>    type of resulting elements
     * @return new RxObservable<R>
     */
    public static <T, R> RxObservable<R> apply(RxObservable<T> source, Function<? super T, RxObservable<? extends R>> mapper) {
        return RxObservable.create(observer -> {
            RxCompositeDisposable composite = new RxCompositeDisposable();
            AtomicInteger activeCount = new AtomicInteger(1); // 1 — родительский поток
            ConcurrentLinkedQueue<Throwable> errors = new ConcurrentLinkedQueue<>();

            RxDisposable parentDisposable = source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    activeCount.incrementAndGet();
                    RxDisposable innerDisposable = mapper.apply(item).subscribe(new RxObserver<R>() {
                        @Override
                        public void onNext(R inner) {
                            observer.onNext(inner);
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
                    });
                    composite.add(innerDisposable);
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
                    if (activeCount.decrementAndGet() == 0) {
                        // если были ошибки — передаем первую
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

            composite.add(parentDisposable);
        });
    }
}
