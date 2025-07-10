package org.example.server.util;

import java.util.concurrent.ConcurrentHashMap;

public class LogDeduplicator {
    private static final ConcurrentHashMap<String, Boolean> logMessages = new ConcurrentHashMap<>();

    public static synchronized boolean shouldLog(String message) {
        // Check if the message exists; if not, mark it for logging
        return logMessages.putIfAbsent(message, true) == null;
    }
}