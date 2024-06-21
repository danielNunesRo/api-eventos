package com.danielnunes.events.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.danielnunes.events.domain.events.Event;

public interface EventRepository extends JpaRepository<Event, UUID> {

}
