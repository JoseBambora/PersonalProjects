package org.jdaextension.configuration;

import java.util.concurrent.TimeUnit;

public record Alarm(Runnable runnable, long delay, TimeUnit timeUnit) {
}
