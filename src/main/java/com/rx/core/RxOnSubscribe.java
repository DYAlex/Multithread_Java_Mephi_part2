package com.rx.core;

/**
 * A functional interface describing the logic of element emitting.
 *
 * @param <T> type of elements
 */
@FunctionalInterface
public interface RxOnSubscribe<T> {
    /**
     * The method that is called when subscribing to pass items to the observer.
     *
     * @param observer target observer
     */
    void subscribe(RxObserver<? super T> observer);
}

