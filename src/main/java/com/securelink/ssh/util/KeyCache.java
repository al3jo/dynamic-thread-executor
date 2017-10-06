package com.securelink.ssh.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class KeyCache {
    private BlockingQueue<String> queue;

    public KeyCache(int capacity) {
        queue = new ArrayBlockingQueue<>(capacity);
    }

    public int getSize() {
        return queue.size();
    }

    public void put(String v) throws InterruptedException {
        queue.put(v);
    }

    public String take() throws InterruptedException {
        return queue.take();
    }
}
