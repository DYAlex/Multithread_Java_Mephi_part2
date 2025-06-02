package com.rx;

import com.rx.core.RxObservable;
import com.rx.operators.*;
import com.rx.schedulers.*;

import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws InterruptedException {
        // Example 1: map + filter + subscribeOn/observeOn
        RxObservable<Integer> source = RxObservable.just(1, 2, 3, 4, 5);
        logger.info("=== map & filter with schedulers ===");
        FilterOperator.apply(MapOperator.apply(source, i -> i * 10), i -> i >= 30).subscribeOn(new RxIOScheduler()).observeOn(new RxComputationScheduler()).subscribe(i -> logger.info("Received: " + i), Throwable::printStackTrace, () -> logger.info("Completed\n"));

        // Waiting for async tasks to complete
        Thread.sleep(500);

        // Example 2: flatMap
        logger.info("=== flatMap Example ===");
        FlatMapOperator.apply(source, i -> RxObservable.just(i, i * i)).subscribe(i -> logger.info("flatMap: " + i));

        // Example 3: merge
        logger.info("\n=== merge Example ===");
        MergeOperator.apply(RxObservable.just("A", "B"), RxObservable.just("1", "2")).subscribe(s -> logger.info("merge: " + s));

        // Example 4: concat
        logger.info("\n=== concat Example ===");
        ConcatOperator.apply(RxObservable.just("X", "Y"), RxObservable.just("Z")).subscribe(s -> logger.info("concat: " + s));
    }
}
