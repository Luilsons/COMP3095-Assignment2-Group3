package com.gbc.eventservice.service;

import com.gbc.eventservice.model.Event;
import java.util.List;
import java.util.Optional;

public interface EventService {
    Event createEvent(Event event);
    List<Event> getAllEvents();
    Optional<Event> getEventById(String id);
    Event updateEvent(String id, Event updatedEvent);
    void deleteEvent(String id);
}
