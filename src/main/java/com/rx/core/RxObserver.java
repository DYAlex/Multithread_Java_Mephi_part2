package com.rx.core;

/**
 * Interface for the reactive stream observer.
 *
 * @param <T> type of data in stream
 */
public interface RxObserver<T> {
    /**
     * Called when a new element is received.
     *
     * @param item stream item
     */
    void onNext(T item);

    /**
     * Called on error in stream
     *
     * @param t the error that occurred
     */
    void onError(Throwable t);

    /**
     * Called on completion of the thread.
     */
    void onComplete();
}

