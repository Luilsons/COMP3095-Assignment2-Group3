package com.example.approvalservice.service;

import com.example.approvalservice.model.Approval;
import com.example.approvalservice.repository.ApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ApprovalService {

    @Autowired
    private ApprovalRepository approvalRepository;

    // Create a new approval
    public Approval createApproval(Approval approval) {
        return approvalRepository.save(approval);
    }

    // Get all approvals
    public List<Approval> getAllApprovals() {
        return approvalRepository.findAll();
    }

    // Get approval by ID
    public Optional<Approval> getApprovalById(String id) {
        return approvalRepository.findById(id);
    }

    // Update an approval (for approving or rejecting an event)
    public Approval updateApproval(String id, Approval updatedApproval) {
        return approvalRepository.findById(id).map(approval -> {
            approval.setStatus(updatedApproval.getStatus());
            approval.setComments(updatedApproval.getComments());
            return approvalRepository.save(approval);
        }).orElseThrow(() -> new RuntimeException("Approval not found"));
    }

    // Delete an approval by ID
    public void deleteApproval(String id) {
        approvalRepository.deleteById(id);
    }

    // Role-based access logic to ensure only staff can approve or reject
    public boolean canUserApprove(String userId, String role) {
        return "staff".equalsIgnoreCase(role);
    }
}