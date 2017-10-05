package com.securelink.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer implements Runnable {

    private KeyCache cache;

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    public Consumer(KeyCache cache) {
        this.cache = cache;
    }

    public void run() {
        try {
            String key = cache.take( );
            logger.debug("Consuming key {}", key);
        } catch (InterruptedException e) {
            logger.info("Interrupted -- Throwing away generated key.", e.getMessage());
        }
    }
}
