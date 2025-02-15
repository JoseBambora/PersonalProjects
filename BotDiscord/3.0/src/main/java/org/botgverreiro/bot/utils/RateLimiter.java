package org.botgverreiro.bot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    private static final long TIME_WINDOW_MS = 60_000;
    private static final ConcurrentHashMap<String, List<Long>> userActions = new ConcurrentHashMap<>();
    private static int MAX_ACTIONS = 10;

    public static boolean isRateLimited(String userId) {
        long currentTime = System.currentTimeMillis();
        List<Long> timestamps = userActions.computeIfAbsent(userId, _ -> new ArrayList<>());
        timestamps.removeIf(timestamp -> currentTime - timestamp > TIME_WINDOW_MS);
        if (timestamps.size() >= MAX_ACTIONS)
            return true;
        else {
            timestamps.add(currentTime);
            return false;
        }
    }

    public static void setRateLimitedTest() {
        MAX_ACTIONS = 2000;
    }
}
