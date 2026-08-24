package com.orderflow.inventory.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("reservations")
@NoArgsConstructor
@Getter
@Setter
public class Reservation {

    @PrimaryKey
    private String sku;

    @Column("total_qty")
    private int totalQty;

    @Column("reserved_qty")
    private int reservedQty;
}
