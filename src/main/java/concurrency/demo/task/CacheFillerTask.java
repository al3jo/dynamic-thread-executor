package concurrency.demo.task;

import concurrency.demo.util.KeyCache;
import concurrency.demo.util.Constants;
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
        logger.info("Threshold 50 : {}, Cores 50 : {}", Constants.THRESHOLD_50, Constants.CORES_50);
        logger.info("Threshold 75 : {}, Cores 75 : {}", Constants.THRESHOLD_75, Constants.CORES_75);
        logger.info("Threshold 90 : {}, Cores 90 : {}", Constants.THRESHOLD_90, Constants.CORES_90);
        logger.info("Threshold 90+: {}, Cores 90+: {}", Constants.THRESHOLD_90 + 1, 1);
        logger.info("Cache max capacity: {}", Constants.COMPUTED_CAPACITY);
    }

    public void run() {
        final Logger logger = LoggerFactory.getLogger(Runnable.class);
        int size = cache.getSize();

        // Tasks
        final KeyProduceTask p = new KeyProduceTask(cache);
//            final Consumer c = new Consumer(cache);

        int currentThreshold = 0, numberOfKeysToProduce = 0; // what threshold and how many cores?
        if (size < Constants.THRESHOLD_50) {
            currentThreshold = Constants.THRESHOLD_50;
            numberOfKeysToProduce = Constants.CORES_50;
        } else if (size < Constants.THRESHOLD_75) {
            currentThreshold = Constants.THRESHOLD_75;
            numberOfKeysToProduce = Constants.CORES_75;
        } else if (size < Constants.THRESHOLD_90) {
            currentThreshold = Constants.THRESHOLD_90;
            numberOfKeysToProduce = Constants.CORES_90;
        } else {
            currentThreshold = Constants.THRESHOLD_90 + 1;
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
