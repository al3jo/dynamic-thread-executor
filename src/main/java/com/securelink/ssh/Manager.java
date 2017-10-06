package com.securelink.ssh;

import static com.securelink.ssh.util.Constants.AVAILABLE_CORES;
import static com.securelink.ssh.util.Constants.COMPUTED_CAPACITY;

import com.securelink.ssh.task.CacheFillerTask;
import com.securelink.ssh.task.CacheStatusTask;
import com.securelink.ssh.util.CustomThreadFactory;
import com.securelink.ssh.util.KeyCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    Manager() {
        initCache();
        initManager();

        initProducer();
        initConsumer();
    }

    private void initManager() {
//        Runnable status = () -> {
//            final Logger logger = LoggerFactory.getLogger(Runnable.class);
//            int size = cache.getSize();
//
//            // Tasks
//            final Producer p = new Producer(cache);
////            final Consumer c = new Consumer(cache);
//
//            int currentThreshold = 0, numberOfKeysToProduce = 0; // what threshold and how many cores?
//            if (size < THRESHOLD_50) {
//                currentThreshold = THRESHOLD_50;
//                numberOfKeysToProduce = CORES_50;
//            } else if (size < THRESHOLD_75) {
//                currentThreshold = THRESHOLD_75;
//                numberOfKeysToProduce = CORES_75;
//            } else if (size < THRESHOLD_90) {
//                currentThreshold = THRESHOLD_90;
//                numberOfKeysToProduce = CORES_90;
//            } else {
//                currentThreshold = THRESHOLD_90 + 1;
//                numberOfKeysToProduce = 1;
//            }
//
//            submitTasks(numberOfKeysToProduce, producerExecutor, p);
////            submitTasks(CORES_75, consumerExecutor, c);
//
//            StringJoiner joiner = new StringJoiner("\n");
//            joiner.add("")
//                .add("STATUS")
//                .add(fmt("At threshold %d, submitted %d tasks", currentThreshold, numberOfKeysToProduce))
//                .add(fmt("Queue [%d/%d]", cache.getSize(), COMPUTED_CAPACITY))
//                .add(fmt("Cores [%d/%d]", numberOfKeysToProduce, AVAILABLE_CORES))
//                .add(fmt("Time elapsed %s", fmt(System.currentTimeMillis() - startTime)));
//            logger.info(joiner.toString());
//        };
        CacheStatusTask cacheStatusTask = new CacheStatusTask(cache, System.currentTimeMillis());
        CacheFillerTask cacheFillerTask = new CacheFillerTask(cache, producerExecutor);

        managerExecutor = Executors.newScheduledThreadPool(2, new CustomThreadFactory("manager"));
        managerExecutor.scheduleAtFixedRate(cacheFillerTask, 250, 500, TimeUnit.MILLISECONDS);
        managerExecutor.scheduleAtFixedRate(cacheStatusTask, 0, 5, TimeUnit.SECONDS);
    }

    private void initCache() {
        cache = new KeyCache(COMPUTED_CAPACITY);
    }

    private void initConsumer() {
        consumerExecutor = Executors.newCachedThreadPool(new CustomThreadFactory("consumer"));
    }

    private void initProducer() {
        producerExecutor = Executors.newFixedThreadPool(AVAILABLE_CORES, new CustomThreadFactory("producer"));
    }



}
