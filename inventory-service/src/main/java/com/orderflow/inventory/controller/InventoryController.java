package com.orderflow.inventory.controller;

import com.orderflow.inventory.dto.ReleaseRequest;
import com.orderflow.inventory.dto.ReleaseResponse;
import com.orderflow.inventory.dto.ReserveRequest;
import com.orderflow.inventory.dto.ReserveResponse;
import com.orderflow.inventory.dto.StockResponse;
import com.orderflow.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/{sku}")
    public StockResponse getStock(@PathVariable String sku) {
        return inventoryService.getStock(sku);
    }

    @PostMapping("/reserve")
    public ReserveResponse reserve(@Valid @RequestBody ReserveRequest request) {
        return inventoryService.reserve(request);
    }

    @PostMapping("/release")
    public ReleaseResponse release(@Valid @RequestBody ReleaseRequest request) {
        return inventoryService.release(request);
    }
}
