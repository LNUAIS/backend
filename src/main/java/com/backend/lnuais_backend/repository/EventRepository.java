package com.backend.lnuais_backend.repository;

import com.backend.lnuais_backend.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}