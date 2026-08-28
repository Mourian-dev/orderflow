package com.orderflow.notification.controller;

import com.orderflow.notification.dto.NotificationResponse;
import com.orderflow.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService service;

    @GetMapping("/{orderIdd}")
    public NotificationResponse get(@PathVariable Long orderId) {
        return NotificationResponse.from(service.get(orderId));
    }
}
