package com.securelink.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringJoiner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Manager {

    private KeyCache cache;
    private ExecutorService consumerExecutor;
    private ExecutorService producerExecutor;
    private ScheduledExecutorService managerExecutor;

    private static final Logger logger = LoggerFactory.getLogger(Manager.class);
    private static final int KEYS_PER_CORE = 200;
//    private static int NUMBER_OF_CORES_TO_USE = 1;
    private static int COMPUTED_CAPACITY;
    private static int CORES_25;
    private static int CORES_50;
    private static int CORES_75;
    private static int THRESHOLD_25;
    private static int THRESHOLD_50;
    private static int THRESHOLD_75;
    private static final int AVAILABLE_CORES = KeyGenerator.getAvailableCores();

    static
    {
        // Use more than one core if available. But never use them all.
//        if (AVAILABLE_CORES > 1)
//        {
//            NUMBER_OF_CORES_TO_USE = AVAILABLE_CORES / 2;
//        }
        COMPUTED_CAPACITY = KEYS_PER_CORE * AVAILABLE_CORES;
        CORES_25 = AVAILABLE_CORES;
        CORES_50 = AVAILABLE_CORES / 2;
        CORES_75 = AVAILABLE_CORES / 4;
        THRESHOLD_25 = (int) (COMPUTED_CAPACITY * 0.25);
        THRESHOLD_50 = (int) (COMPUTED_CAPACITY * 0.50);
        THRESHOLD_75 = (int) (COMPUTED_CAPACITY * 0.75);
    }

    private static long startTime;

    Manager() {
        initManager();
        initCache();
        initProducer();
        initConsumer();

        logger.info("Initial configuration");
        logger.info("Threshold 25 : {}, Cores 25 : {}", THRESHOLD_25, CORES_25);
        logger.info("Threshold 50 : {}, Cores 50 : {}", THRESHOLD_50, CORES_50);
        logger.info("Threshold 75 : {}, Cores 75 : {}", THRESHOLD_75, CORES_75);
        logger.info("Threshold 75+: {}, Cores 75+: {}", THRESHOLD_75+1, 1);
        logger.info("Cache max capacity: {}", COMPUTED_CAPACITY);


        startTime = System.currentTimeMillis();
    }

    private void initManager() {
        Runnable status = () -> {
            final Logger logger = LoggerFactory.getLogger(Runnable.class);
            int size = cache.getSize();

            // Tasks
            final Producer p = new Producer(cache);
            final Consumer c = new Consumer(cache);

            int currentThreshold = 0, numberOfKeysToProduce = 0; // what threshold and how many cores?
            if (size < THRESHOLD_25) {
                currentThreshold = THRESHOLD_25;
                numberOfKeysToProduce = CORES_25;
            } else if (size < THRESHOLD_50) {
                currentThreshold = THRESHOLD_50;
                numberOfKeysToProduce = CORES_50;
            } else if (size < THRESHOLD_75) {
                currentThreshold = THRESHOLD_75;
                numberOfKeysToProduce = CORES_75;
            } else {
                currentThreshold = THRESHOLD_75 + 1;
                numberOfKeysToProduce = 1;
            }

            submitTasks(numberOfKeysToProduce, producerExecutor, p);
//            submitTasks(CORES_75, consumerExecutor, c);

            StringJoiner joiner = new StringJoiner("\n");
            joiner.add("")
                .add("STATUS")
                .add(fmt("At threshold %d, submitted %d tasks", currentThreshold, numberOfKeysToProduce))
                .add(fmt("Queue [%d/%d]", cache.getSize(), COMPUTED_CAPACITY))
                .add(fmt("Cores [%d/%d]", c, AVAILABLE_CORES))
                .add(fmt("Time elapsed %s", fmt(System.currentTimeMillis() - startTime)));
            logger.info(joiner.toString());
        };
        managerExecutor = Executors.newSingleThreadScheduledExecutor(new CustomThreadFactory("manager"));
        managerExecutor.scheduleAtFixedRate(status, 250, 500, TimeUnit.MILLISECONDS);
    }

    private void submitTasks(int n, ExecutorService x, Runnable task) {
        for (int i = 0; i < n; i++) {
            x.submit(task);
        }
    }

    private void initCache() {
        cache = new KeyCache(COMPUTED_CAPACITY);
    }

    private void initConsumer() {
        consumerExecutor = Executors.newCachedThreadPool(new CustomThreadFactory("consumer"));
    }

    private void initProducer() {
        producerExecutor = Executors.newCachedThreadPool(new CustomThreadFactory("producer"));
    }

    private String fmt(String template, Object... args) {
        return String.format(template, args);
    }

    private static String fmt(long ms) {
        return String.format("%02d:%02d:%02d.%02d",
                TimeUnit.MILLISECONDS.toHours(ms),
                TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)),
                TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)),
                TimeUnit.MILLISECONDS.toMillis(ms) - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(ms))
        );
    }

}
