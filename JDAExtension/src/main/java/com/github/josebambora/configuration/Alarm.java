package com.github.josebambora.configuration;

import java.util.concurrent.TimeUnit;

public record Alarm(Runnable runnable, long delay, TimeUnit timeUnit) {
}
