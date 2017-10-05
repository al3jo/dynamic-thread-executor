package com.securelink.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Producer implements Runnable {

    private KeyCache cache;

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    public Producer(KeyCache cache) {
        this.cache = cache;
    }

    public void run() {
        try {
            String fingerprint = KeyGenerator.generate();
            cache.put(fingerprint);
            logger.debug("Generated {}", fingerprint);
        } catch (InterruptedException e) {
            logger.info("Interrupted -- Throwing away generated key.", e.getMessage());
        }
    }

}
