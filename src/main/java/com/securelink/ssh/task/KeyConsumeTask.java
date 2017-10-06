package com.securelink.ssh.task;

import com.securelink.ssh.util.KeyCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KeyConsumeTask implements Runnable {

    private KeyCache cache;

    private static final Logger logger = LoggerFactory.getLogger(KeyConsumeTask.class);

    public KeyConsumeTask(KeyCache cache) {
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