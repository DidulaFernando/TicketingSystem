package org.example.server.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class ConsoleLogCapture {
    private static final List<String> logBuffer = Collections.synchronizedList(new ArrayList<>());
    private static final int MAX_LOGS = 100;

    private static final List<Consumer<String>> listeners = new ArrayList<>();
    private static final PrintStream originalOut = System.out;

    static {
        System.setOut(new PrintStream(new OutputStream() {
            private final StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                if (b == '\n') {
                    String log = buffer.toString().trim();
                    buffer.setLength(0);

                    if (!log.isEmpty()) {
                        String cleanedLog = removePrefixes(log);
                        addToBuffer(cleanedLog);
                        notifyListeners(cleanedLog);
                        originalOut.println(log);
                    }
                } else {
                    buffer.append((char) b);
                }
            }
        }));
    }

    public static void addListener(Consumer<String> listener) {
        listeners.add(listener);
    }

    public static void removeListener(Consumer<String> listener) {
        listeners.remove(listener);
    }

    private static synchronized void notifyListeners(String log) {
        listeners.forEach(listener -> listener.accept(log));
    }

    private static String removePrefixes(String log) {
        return log.replaceFirst("^[\\d\\-:\\s]+(INFO|WARN|ERROR|DEBUG)\\s+[\\w\\.]+\\s+-\\s+", "").trim();
    }

    private static synchronized void addToBuffer(String log) {
        logBuffer.add(log);
        if (logBuffer.size() > MAX_LOGS) {
            logBuffer.remove(0);
        }
    }

    public static synchronized List<String> getLogs() {
        return new ArrayList<>(logBuffer);
    }

    public static synchronized void clearLogs() {
        logBuffer.clear();
        listeners.clear();
    }
}