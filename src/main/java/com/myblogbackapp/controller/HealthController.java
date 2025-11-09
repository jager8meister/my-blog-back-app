package com.myblogbackapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Service health checks")
public class HealthController {

    @GetMapping("/api/health")
    @Operation(summary = "Simple health check endpoint")
    public String health() {
        return "Backend is up";
    }
}
