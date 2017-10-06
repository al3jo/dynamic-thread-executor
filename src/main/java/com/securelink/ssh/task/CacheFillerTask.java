package com.securelink.ssh.task;

import static com.securelink.ssh.util.Constants.COMPUTED_CAPACITY;
import static com.securelink.ssh.util.Constants.CORES_50;
import static com.securelink.ssh.util.Constants.CORES_75;
import static com.securelink.ssh.util.Constants.CORES_90;
import static com.securelink.ssh.util.Constants.THRESHOLD_50;
import static com.securelink.ssh.util.Constants.THRESHOLD_75;
import static com.securelink.ssh.util.Constants.THRESHOLD_90;

import com.securelink.ssh.util.KeyCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutorService;

public class CacheFillerTask implements Runnable {

    private ExecutorService executor;
    private KeyCache cache;

    private static final Logger logger = LoggerFactory.getLogger(KeyProduceTask.class);

    public CacheFillerTask(KeyCache cache, ExecutorService executor) {
        this.cache = cache;

        logger.info("Initializing Manager");
        logger.info("Threshold 50 : {}, Cores 50 : {}", THRESHOLD_50, CORES_50);
        logger.info("Threshold 75 : {}, Cores 75 : {}", THRESHOLD_75, CORES_75);
        logger.info("Threshold 90 : {}, Cores 90 : {}", THRESHOLD_90, CORES_90);
        logger.info("Threshold 90+: {}, Cores 90+: {}", THRESHOLD_90 + 1, 1);
        logger.info("Cache max capacity: {}", COMPUTED_CAPACITY);
    }

    public void run() {
        final Logger logger = LoggerFactory.getLogger(Runnable.class);
        int size = cache.getSize();

        // Tasks
        final KeyProduceTask p = new KeyProduceTask(cache);
//            final Consumer c = new Consumer(cache);

        int currentThreshold = 0, numberOfKeysToProduce = 0; // what threshold and how many cores?
        if (size < THRESHOLD_50) {
            currentThreshold = THRESHOLD_50;
            numberOfKeysToProduce = CORES_50;
        } else if (size < THRESHOLD_75) {
            currentThreshold = THRESHOLD_75;
            numberOfKeysToProduce = CORES_75;
        } else if (size < THRESHOLD_90) {
            currentThreshold = THRESHOLD_90;
            numberOfKeysToProduce = CORES_90;
        } else {
            currentThreshold = THRESHOLD_90 + 1;
            numberOfKeysToProduce = 1;
        }

        submitTasks(numberOfKeysToProduce, executor, p);
    }

    private void submitTasks(int n, ExecutorService x, Runnable task) {
        for (int i = 0; i < n; i++) {
            x.submit(task);
        }
    }

}
