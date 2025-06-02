package com.rx.schedulers;

/**
 * Scheduler interface
 */
public interface RxScheduler {
    /**
     * Schedule task
     *
     * @param task Runnable task
     */
    void schedule(Runnable task);
}

