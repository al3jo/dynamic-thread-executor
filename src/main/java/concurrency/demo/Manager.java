package concurrency.demo;

import concurrency.demo.task.CacheFillerTask;
import concurrency.demo.task.CacheStatusTask;
import concurrency.demo.util.CustomThreadFactory;
import concurrency.demo.util.KeyCache;
import concurrency.demo.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Manager {

    private KeyCache cache;
    private ExecutorService consumerExecutor;
    private ScheduledExecutorService producerExecutor;
    private ScheduledExecutorService managerExecutor;

    private static final Logger logger = LoggerFactory.getLogger(Manager.class);

    Manager() {
        initCache();
        initProducer();
        initConsumer();
        initManager();
    }

    private void initManager() {
        CacheStatusTask cacheStatusTask = new CacheStatusTask(cache, System.currentTimeMillis());
        CacheFillerTask cacheFillerTask = new CacheFillerTask(cache, producerExecutor);

        managerExecutor = Executors.newScheduledThreadPool(2, new CustomThreadFactory("manager"));
        managerExecutor.scheduleAtFixedRate(cacheFillerTask, 0, 500, TimeUnit.MILLISECONDS);
//        managerExecutor.scheduleAtFixedRate(cacheStatusTask, 0, 5, TimeUnit.SECONDS);
    }

    private void initCache() {
        cache = new KeyCache(Constants.COMPUTED_CAPACITY);
    }

    private void initConsumer() {
        consumerExecutor = Executors.newCachedThreadPool(new CustomThreadFactory("consumer"));
    }

    private void initProducer() {
        producerExecutor = Executors.newScheduledThreadPool(Constants.AVAILABLE_CORES, new CustomThreadFactory("producer"));
    }

}
