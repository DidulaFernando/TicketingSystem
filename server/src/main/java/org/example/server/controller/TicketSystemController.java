package org.example.server.controller;

import org.example.server.model.SystemConfig;
import org.example.server.service.TicketSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class TicketSystemController {

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
}