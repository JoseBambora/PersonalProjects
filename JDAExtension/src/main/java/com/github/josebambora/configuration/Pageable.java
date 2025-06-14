package com.github.josebambora.configuration;

import java.time.LocalDateTime;

public class Pageable {
    private int page;
    private final LocalDateTime localDateTime;

    protected Pageable() {
        this.page = 0;
        localDateTime = LocalDateTime.now();
    }

    protected void next() {
        this.page++;
    }

    protected void previous() {
        this.page--;
    }

    public int getPage() {
        return page;
    }

    public boolean isOver() {
        return localDateTime.plusMinutes(1).isBefore(LocalDateTime.now());
    }
}
