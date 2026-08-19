package com.orderflow.inventory.repository;

import com.orderflow.inventory.entity.Reservation;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends CassandraRepository<Reservation, String> {

}
