package com.securelink.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringJoiner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Manager {

    private KeyCache cache;
    private ScheduledExecutorService consumer;
    private ScheduledExecutorService generator;
    private ScheduledExecutorService manager;

    private static final Logger logger = LoggerFactory.getLogger(Manager.class);
    private static final int KEYS_PER_CORE = 200;
    private static int NUMBER_OF_CORES_TO_USE = 1;
    private static int COMPUTED_CAPACITY;

    static
    {
        // Use more than one core if available. But never use them all.
        int cores = KeyGenerator.getAvailableCores();
        if (cores > 1)
        {
            NUMBER_OF_CORES_TO_USE = cores / 2;
        }
        COMPUTED_CAPACITY = KEYS_PER_CORE * NUMBER_OF_CORES_TO_USE;
    }

    static long startTime;

    public Manager() {
        cache = new KeyCache(COMPUTED_CAPACITY);

        generator = Executors.newScheduledThreadPool(NUMBER_OF_CORES_TO_USE);

        Producer producer = new Producer(cache);
        startTime = System.currentTimeMillis();
        for (int i = 0; i< NUMBER_OF_CORES_TO_USE; i++)
            generator.scheduleAtFixedRate(producer, 0, 1, TimeUnit.MILLISECONDS);

        Runnable status = () -> {
            final Logger logger = LoggerFactory.getLogger(Runnable.class);
            StringJoiner joiner = new StringJoiner("\n");
            joiner.add("")
                .add("STATUS")
                .add(String.format("Queue [%d/%d]", cache.getSize(),  COMPUTED_CAPACITY))
                .add(String.format("Cores [%d/%d]", NUMBER_OF_CORES_TO_USE, KeyGenerator.getAvailableCores()))
                .add(String.format("Time elapsed %s", fmt(System.currentTimeMillis() - startTime)));
            logger.info(joiner.toString());
        };
        manager = Executors.newSingleThreadScheduledExecutor();
        manager.scheduleAtFixedRate(status, 5, 1, TimeUnit.SECONDS);

        Consumer c = new Consumer(cache);
        int threads = NUMBER_OF_CORES_TO_USE -1;
        consumer = Executors.newScheduledThreadPool(threads);
        for (int i = 0; i< threads; i++)
        consumer.scheduleAtFixedRate(c, 10000, 250, TimeUnit.MILLISECONDS);
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
