package com.securelink.ssh.task;

import com.securelink.ssh.util.KeyCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.securelink.ssh.util.Constants.COMPUTED_CAPACITY;
import static com.securelink.ssh.util.Utils.fmt;

import java.util.StringJoiner;

public class CacheStatusTask implements Runnable {

    private KeyCache cache;
    private long startTime;

    private final Logger logger = LoggerFactory.getLogger(CacheStatusTask.class);

    public CacheStatusTask(KeyCache cache, long startTime) {
        this.cache = cache;
        this.startTime = startTime;
    }

    @Override
    public void run() {
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("")
            .add("STATUS")
//            .add(fmt("At threshold %d, submitted %d tasks", currentThreshold, numberOfKeysToProduce))
            .add(fmt("Queue [%d/%d]", cache.getSize(), COMPUTED_CAPACITY))
//            .add(fmt("Cores [%d/%d]", numberOfKeysToProduce, AVAILABLE_CORES))
            .add(fmt("Time elapsed %s", fmt(System.currentTimeMillis() - startTime)));
        logger.info(joiner.toString());
    }
}
