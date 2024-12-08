package com.example.approvalservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "approvals")
public class Approval {
    @Id
    private String id;
    private String eventId;
    private String approverId;
    private String status;
    private String comments;
}
