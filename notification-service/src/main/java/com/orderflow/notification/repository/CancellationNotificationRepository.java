package com.orderflow.notification.repository;

import com.orderflow.notification.entity.CancellationNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CancellationNotificationRepository extends JpaRepository<CancellationNotification, Long> { }
