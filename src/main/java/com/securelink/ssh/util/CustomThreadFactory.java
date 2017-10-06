package com.securelink.ssh.util;

import java.util.concurrent.ThreadFactory;

public class CustomThreadFactory implements ThreadFactory {
    private int counter = 0;
    private String name;

    private static final String NAME_TEMPLATE = "%s-thread-%d";

    public CustomThreadFactory(String name) {
        this.name = name;
        counter = 0;
    }

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, String.format(NAME_TEMPLATE, name, ++counter));
    }
}
