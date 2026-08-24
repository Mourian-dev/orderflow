package com.orderflow.inventory.service;

import com.orderflow.inventory.dto.ReleaseRequest;
import com.orderflow.inventory.dto.ReleaseResponse;
import com.orderflow.inventory.dto.ReserveRequest;
import com.orderflow.inventory.dto.ReserveResponse;
import com.orderflow.inventory.dto.StockResponse;
import com.orderflow.inventory.entity.Reservation;
import com.orderflow.inventory.exception.InsufficientStockException;
import com.orderflow.inventory.exception.SkuNotFoundException;
import com.orderflow.inventory.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryService {

    public static final String INVENTORY_CACHE = "inventory";

    private final ReservationRepository repository;

    @Cacheable(cacheNames = INVENTORY_CACHE, key = "#sku")
    public StockResponse getStock(String sku) {
        log.info("CACHE MISS -- Cassandra query: SELECT * FROM reservations WHERE sku = {}", sku);

        Reservation reservation = repository.findById(sku).orElseThrow(() -> new SkuNotFoundException(sku));

        return new StockResponse(
                reservation.getSku(), reservation.getTotalQty(), reservation.getReservedQty(), reservation.getTotalQty() - reservation.getReservedQty()
        );
    }

    @CacheEvict(cacheNames = INVENTORY_CACHE, key = "#request.sku()")
    public ReserveResponse reserve(ReserveRequest request) {
        Reservation reservation = repository.findById(request.sku()).orElseThrow(() -> new SkuNotFoundException(request.sku()));

        int available = reservation.getTotalQty() - reservation.getReservedQty();

        if(available < request.qty()) {
            throw new InsufficientStockException(request.sku(), request.qty(), available);
        }

        reservation.setReservedQty(reservation.getReservedQty() + request.qty());
        repository.save(reservation);

        return new ReserveResponse(
                reservation.getSku(), reservation.getReservedQty(), reservation.getTotalQty() - reservation.getReservedQty()
        );
    }

    @CacheEvict(cacheNames = INVENTORY_CACHE, key = "#request.sku()")
    public ReleaseResponse release(ReleaseRequest request) {
        Reservation reservation = repository.findById(request.sku()).orElseThrow(() -> new SkuNotFoundException(request.sku()));

        reservation.setReservedQty(Math.max(0, reservation.getReservedQty() - request.qty()));
        repository.save(reservation);

        return new ReleaseResponse(
                reservation.getSku(),
                reservation.getReservedQty(),
                reservation.getTotalQty() - reservation.getReservedQty()
        );
    }
}
