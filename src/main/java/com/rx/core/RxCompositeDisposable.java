package com.rx.core;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CompositeDisposable for group cancellation of multiple subscriptions.
 */
public class RxCompositeDisposable {
    private final Set<RxDisposable> disposables = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * Adds Disposable to group.
     *
     * @param d Disposable for adding
     */
    public void add(RxDisposable d) {
        disposables.add(d);
    }

    /**
     * Removes Disposable from group.
     *
     * @param d Disposable for removal
     */
    public void remove(RxDisposable d) {
        disposables.remove(d);
    }

    /**
     * Cancel all subscriptions in the group.
     */
    public void dispose() {
        for (RxDisposable d : disposables) {
            d.dispose();
        }
        disposables.clear();
    }

    /**
     * Checks if all subscriptions are canceled.
     *
     * @return true if all subscriptions are cancelled
     */
    public boolean isDisposed() {
        return disposables.stream().allMatch(RxDisposable::isDisposed);
    }
}

