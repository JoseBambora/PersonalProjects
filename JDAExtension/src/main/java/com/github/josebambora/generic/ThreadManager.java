package com.github.josebambora.generic;

public interface ThreadManager {
    void runRequest(Runnable runnable);

    void shutDown();
}
