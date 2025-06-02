package com.rx.core;

import com.rx.schedulers.RxScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * The main class of the reactive stream.
 *
 * @param <T> type of elements
 */
public class RxObservable<T> {
    private static final Logger log = LoggerFactory.getLogger(RxObservable.class);

    private final RxOnSubscribe<T> source;

    private RxObservable(RxOnSubscribe<T> source) {
        this.source = source;
    }

    /**
     * A factory method for creating an Observable.
     *
     * @param source logic of element emission
     * @param <T>    type of elements
     * @return new RxObservable
     */
    public static <T> RxObservable<T> create(RxOnSubscribe<T> source) {
        log.debug("Creating RxObservable via create()");
        return new RxObservable<>(source);
    }

    /**
     * The factory method for a single emitter.
     *
     * @param item element
     * @param <T>  type of element
     * @return An observable that emits a single element and completes
     */
    public static <T> RxObservable<T> just(T item) {
        return create(observer -> {
            observer.onNext(item);
            observer.onComplete();
        });
    }

    /**
     * Creates an Observable that emits the passed elements and completes the stream immediately.
     *
     * @param items elements for emitting
     * @param <T>   type of element
     * @return new RxObservable
     */
    @SafeVarargs
    public static <T> RxObservable<T> just(T... items) {
        return create(observer -> {
            Arrays.stream(items).forEach(observer::onNext);
            observer.onComplete();
        });
    }

    /**
     * A subscription with a full set of handlers.
     *
     * @param onNext     action on new element
     * @param onError    action on error
     * @param onComplete action on complete
     * @return Rx Disposable for subscription cancellation
     */
    public RxDisposable subscribe(Consumer<? super T> onNext, Consumer<Throwable> onError, Runnable onComplete) {
        RxObserver<T> obs = new RxObserver<T>() {
            @Override
            public void onNext(T item) {
                onNext.accept(item);
            }

            @Override
            public void onError(Throwable t) {
                onError.accept(t);
            }

            @Override
            public void onComplete() {
                onComplete.run();
            }
        };
        return subscribe(obs);
    }

    /**
     * Subscription with onNext handler.
     *
     * @param onNext action on new element
     * @return RxDisposable for subscription cancellation
     */
    public RxDisposable subscribe(Consumer<? super T> onNext) {
        return subscribe(onNext, Throwable::printStackTrace, () -> {
        });
    }

    /**
     * Basic subscribe, returns Disposable.
     *
     * @param observer observer
     * @return RxDisposable for subscription cancellation
     */
    public RxDisposable subscribe(RxObserver<? super T> observer) {
        log.debug("New subscription for RxObservable");
        RxDisposable disposable = new RxDisposable();
        try {
            source.subscribe(new RxObserver<T>() {
                @Override
                public void onNext(T item) {
                    if (!disposable.isDisposed()) {
                        observer.onNext(item);
                    }
                }

                @Override
                public void onError(Throwable t) {
                    if (!disposable.isDisposed()) {
                        observer.onError(t);
                    }
                }

                @Override
                public void onComplete() {
                    if (!disposable.isDisposed()) {
                        observer.onComplete();
                    }
                }
            });
        } catch (Throwable t) {
            observer.onError(t);
        }
        return disposable;
    }

    /**
     * The subscription is executed in the specified scheduler.
     *
     * @param scheduler scheduler for calling source.subscribe()
     * @return a new Observable whose subscription has been postponed to the scheduler
     */
    public RxObservable<T> subscribeOn(RxScheduler scheduler) {
        return RxObservable.create(observer -> scheduler.schedule(() -> this.subscribe(observer)));
    }

    /**
     * The on Next/on Error/incomplete issue occurs in the specified scheduler.
     *
     * @param scheduler scheduler for event handling
     * @return a new Observable whose events are passed to the scheduler
     */
    public RxObservable<T> observeOn(RxScheduler scheduler) {
        return RxObservable.create(observer -> this.subscribe(new RxObserver<T>() {
            @Override
            public void onNext(T item) {
                scheduler.schedule(() -> observer.onNext(item));
            }

            @Override
            public void onError(Throwable t) {
                scheduler.schedule(() -> observer.onError(t));
            }

            @Override
            public void onComplete() {
                scheduler.schedule(observer::onComplete);
            }
        }));
    }
}