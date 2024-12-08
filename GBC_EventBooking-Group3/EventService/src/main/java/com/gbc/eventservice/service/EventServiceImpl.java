package com.gbc.eventservice.service;

import com.gbc.eventservice.model.Event;
import com.gbc.eventservice.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private RestTemplate restTemplate;

    private static final String USER_SERVICE_URL = "http://user-service/api/users";

    @Override
    public Event createEvent(Event event) {
        // Construct the URL to fetch the user role
        String userRoleUrl = USER_SERVICE_URL + "/" + event.getOrganizerId() + "/role";

        // Call UserService to get the user's role
        String userRole = restTemplate.getForObject(userRoleUrl, String.class);

        // Check if the user has the necessary role to create the event
        if (!isAllowedToOrganizeEvent(userRole, event)) {
            throw new RuntimeException("User does not have permission to organize this event.");
        }

        // If allowed, save the event
        return eventRepository.save(event);
    }


    private boolean isAllowedToOrganizeEvent(String userRole, Event event) {
        if ("faculty".equals(userRole)) {
            return true;
        } else if ("student".equals(userRole) && event.getExpectedAttendees() <= 50) {
            return true;
        }
        return false;
    }

    @Override
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Override
    public Optional<Event> getEventById(String id) {
        return eventRepository.findById(id);
    }

    @Override
    public Event updateEvent(String id, Event updatedEvent) {
        return eventRepository.findById(id).map(event -> {
            event.setEventName(updatedEvent.getEventName());
            event.setOrganizerId(updatedEvent.getOrganizerId());
            event.setEventType(updatedEvent.getEventType());
            event.setExpectedAttendees(updatedEvent.getExpectedAttendees());
            event.setRoomId(updatedEvent.getRoomId());
            return eventRepository.save(event);
        }).orElseThrow(() -> new RuntimeException("Event not found"));
    }

    @Override
    public void deleteEvent(String id) {
        eventRepository.deleteById(id);
    }
}
