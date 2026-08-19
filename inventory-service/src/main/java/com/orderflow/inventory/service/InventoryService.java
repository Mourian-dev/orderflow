package com.orderflow.inventory.service;

import com.orderflow.inventory.dto.ReleaseRequest;
import com.orderflow.inventory.dto.ReleaseResponse;
import com.orderflow.inventory.dto.ReserveRequest;
import com.orderflow.inventory.dto.ReserveResponse;
import com.orderflow.inventory.entity.Reservation;
import com.orderflow.inventory.exception.InsufficientStockException;
import com.orderflow.inventory.exception.SkuNotFoundException;
import com.orderflow.inventory.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final ReservationRepository repository;

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
