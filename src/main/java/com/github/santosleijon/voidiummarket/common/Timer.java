package com.github.santosleijon.voidiummarket.common;

public class Timer implements AutoCloseable {

    private final long startTime = System.currentTimeMillis();
    private final String description;

    public Timer(String description) {
        this.description = description;
    }

    @Override
    public void close() {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("'"+description + "': " + executionTime + " milliseconds");
    }
}
