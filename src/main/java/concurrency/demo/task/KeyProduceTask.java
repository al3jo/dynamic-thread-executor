package concurrency.demo.task;

import concurrency.demo.util.KeyCache;
import concurrency.demo.util.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyProduceTask implements Runnable {

    private KeyCache cache;

    private static final Logger logger = LoggerFactory.getLogger(KeyProduceTask.class);

    public KeyProduceTask(KeyCache cache) {
        this.cache = cache;
    }

    @Override
    public void run() {
        try {
            logger.info("Producing 1 key");
            String fingerprint = KeyGenerator.generate();
            cache.put(fingerprint);
            logger.debug("Generated {}", fingerprint);
        } catch (InterruptedException e) {
            logger.info("Interrupted -- Throwing away generated key.", e.getMessage());
        }
    }

}
