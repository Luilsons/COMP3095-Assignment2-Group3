package com.example.approvalservice.controller;

import com.example.approvalservice.model.Approval;
import com.example.approvalservice.service.ApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/approvals")
public class ApprovalController {

    private final ApprovalService approvalService;
    private static final Logger logger = LoggerFactory.getLogger(ApprovalController.class);

    @Autowired
    public ApprovalController(ApprovalService approvalService) {
        this.approvalService = approvalService;
    }

    @PostMapping
    public ResponseEntity<Approval> createApproval(@RequestBody Approval approval) {
        Approval createdApproval = approvalService.createApproval(approval);
        return new ResponseEntity<>(createdApproval, HttpStatus.CREATED); // 201 Created
    }

    @GetMapping
    public ResponseEntity<List<Approval>> getAllApprovals() {
        List<Approval> approvals = approvalService.getAllApprovals();
        return new ResponseEntity<>(approvals, HttpStatus.OK); // 200 OK
    }

    @GetMapping("/{id}")
    public ResponseEntity<Approval> getApprovalById(@PathVariable String id) {
        Approval approval = approvalService.getApprovalById(id)
                .orElseThrow(() -> new NoSuchElementException("Approval not found with id: " + id));
        return new ResponseEntity<>(approval, HttpStatus.OK); // 200 OK
    }

    @PutMapping("/{id}")
    public ResponseEntity<Approval> updateApproval(
            @PathVariable String id,
            @RequestBody Approval updatedApproval,
            @RequestHeader("User-Role") String userRole) {

        if (userRole == null || userRole.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST); // 400 Bad Request with null body
        }

        if (!approvalService.canUserApprove(updatedApproval.getApproverId(), userRole)) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN); // 403 Forbidden with null body
        }

        Approval approval = approvalService.updateApproval(id, updatedApproval);
        return new ResponseEntity<>(approval, HttpStatus.OK); // 200 OK
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApproval(@PathVariable String id) {
        approvalService.deleteApproval(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException ex) {
        logger.error("Resource not found: ", ex);
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND); // 404 Not Found
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleInternalError(Exception ex) {
        logger.error("Internal server error: ", ex);
        return new ResponseEntity<>("An error occurred. Please contact support if the issue persists.", HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
    }
}
