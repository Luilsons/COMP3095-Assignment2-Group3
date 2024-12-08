package com.gbc.eventservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "events")
public class Event {
    @Id
    private String id;
    private String eventName;
    private String organizerId;
    private String eventType;
    private int expectedAttendees;
    private String roomId;
}
