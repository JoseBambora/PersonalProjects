package com.github.josebambora.configuration;

import com.github.josebambora.generic.ThreadManager;

public class DefaultThreadManager implements ThreadManager {
    @Override
    public void runRequest(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void shutDown() {

    }
}
