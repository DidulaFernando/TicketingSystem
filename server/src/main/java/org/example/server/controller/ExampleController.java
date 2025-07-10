package org.example.server.controller;

import org.example.server.service.TicketSystemService;
import org.example.server.model.SystemConfig;
import org.example.server.util.ConsoleLogCapture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RestController
@RequestMapping("/api/example")
public class ExampleController {

    @Autowired
    private TicketSystemService ticketSystemService;

    @PostMapping("/start")
    public ResponseEntity<String> startSystem(@RequestBody SystemConfig config) {
        ticketSystemService.startSystem(config);
        return ResponseEntity.ok("System started successfully!");
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopSystem() {
        ticketSystemService.stopSystem();
        return ResponseEntity.ok("System stopped successfully!");
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getTicketStatus() {
        return ResponseEntity.ok(ticketSystemService.getTicketStatus());
    }

    @GetMapping("/logs")
    public ResponseEntity<List<String>> getLogs() {
        List<String> logs = ConsoleLogCapture.getLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping(value = "/logs/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamLogs() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        Consumer<String> listener = log -> {
            try {
                emitter.send(log);
            } catch (IOException e) {
                emitter.completeWithError(e);
            }
        };

        ConsoleLogCapture.addListener(listener);

        emitter.onCompletion(() -> ConsoleLogCapture.removeListener(listener));  // Clean up the listener on completion
        emitter.onTimeout(() -> ConsoleLogCapture.removeListener(listener));     // Clean up the listener on timeout
        emitter.onError((ex) -> ConsoleLogCapture.removeListener(listener));     // Clean up the listener on error

        return emitter;
    }
}