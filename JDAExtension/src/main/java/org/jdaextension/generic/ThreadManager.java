package org.jdaextension.generic;

public interface ThreadManager {
    void runRequest(Runnable runnable);
    void shutDown();
}
