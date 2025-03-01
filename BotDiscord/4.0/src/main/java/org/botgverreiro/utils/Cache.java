package org.botgverreiro.utils;

import com.github.benmanes.caffeine.cache.AsyncLoadingCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Cache<T1, T2> {
    private final AsyncLoadingCache<T1, CompletionStage<List<T2>>> cacheObj;

    public Cache(Function<T1, CompletionStage<List<T2>>> fetchFunction) {
        cacheObj = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .maximumSize(100)
                .buildAsync(fetchFunction::apply);
    }

    public CompletionStage<List<T2>> get(T1 key) {
        return cacheObj.get(key).thenCompose(l -> l);
    }
}
