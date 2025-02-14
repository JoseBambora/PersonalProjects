package org.jdaextension.configuration;

import org.jdaextension.generic.ThreadManager;

public class DefaultThreadManager implements ThreadManager {
    @Override
    public void runRequest(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void shutDown() {

    }
}
