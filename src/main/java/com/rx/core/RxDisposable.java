package com.rx.core;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of the subscription cancellation mechanism.
 */
public class RxDisposable {
    private final AtomicBoolean disposed = new AtomicBoolean(false);

    /**
     * Cancels the subscription and stops event delivery.
     */
    public void dispose() {
        disposed.set(true);
    }

    /**
     * Checks if the subscription has been cancelled.
     *
     * @return true, if already cancelled
     */
    public boolean isDisposed() {
        return disposed.get();
    }
}

