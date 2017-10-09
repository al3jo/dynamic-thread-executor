package concurrency.demo.task;

import static concurrency.demo.util.Constants.AVAILABLE_CORES;
import static concurrency.demo.util.Utils.fmt;
import concurrency.demo.util.KeyCache;
import concurrency.demo.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CacheFillerTask implements Runnable {

    private ScheduledExecutorService executor;
    private KeyCache cache;

    private static final Logger logger = LoggerFactory.getLogger(KeyProduceTask.class);

    public CacheFillerTask(KeyCache cache, ScheduledExecutorService executor) {
        this.cache = cache;

        logger.info("Initializing Manager");
        logger.info("Threshold 50 : {}, Cores 50 : {}", Constants.THRESHOLD_50, Constants.CORES_50);
        logger.info("Threshold 75 : {}, Cores 75 : {}", Constants.THRESHOLD_75, Constants.CORES_75);
        logger.info("Threshold 90 : {}, Cores 90 : {}", Constants.THRESHOLD_90, Constants.CORES_90);
        logger.info("Threshold 90+: {}, Cores 90+: {}", Constants.THRESHOLD_90 + 1, 1);
        logger.info("Cache max capacity: {}", Constants.COMPUTED_CAPACITY);
    }

    @Override
    public void run() {
        final Logger logger = LoggerFactory.getLogger(Runnable.class);
        int size = cache.getSize();

        // Tasks
//        final KeyProduceTask p = new KeyProduceTask(cache);
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

        logger.info(fmt("At threshold %d, submitted %d tasks", currentThreshold, numberOfKeysToProduce));
        logger.info(fmt("Cores [%d/%d]", numberOfKeysToProduce, AVAILABLE_CORES));
        logger.info("Submitting {} of key generation tasks", numberOfKeysToProduce);
        executor.schedule(new KeyProduceTask(cache), 0, TimeUnit.SECONDS);
//        submitTasks(numberOfKeysToProduce, executor, p);

    }

    private void submitTasks(int n, ScheduledExecutorService x, Runnable task) {
        for (int i = 0; i < n; i++) {
//            x.submit(task);
//            x.schedule(task, 0, TimeUnit.SECONDS);
        }
    }

}
