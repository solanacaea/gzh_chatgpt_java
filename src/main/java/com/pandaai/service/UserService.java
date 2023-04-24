package com.pandaai.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class UserService {

    private static final String USER_USAGE_LIMIT_COUNT = "USER_USAGE_LIMIT_COUNT";
    private static final String USER_USAGE_LIMIT_TOKEN = "USER_USAGE_LIMIT_TOKEN";
    private static final String MAX_TOKENS = "MAX_TOKENS";
    private static final String PARALLEL_CALLS = "PARALLEL_CALLS";
    private static final int maxThreads = 10;

    private final AtomicInteger count = new AtomicInteger();

    public String checkUserDosage() {
        if (get() >= maxThreads) {
            return "服务器繁忙，请稍后再试。";
        }
        return null;
    }

    public int get() {
        return count.get();
    }

    public int plus() {
        return count.incrementAndGet();
    }

    public int minus() {
        return count.decrementAndGet();
    }

}
