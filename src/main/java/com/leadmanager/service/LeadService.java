package com.leadmanager.service;

import com.leadmanager.model.Lead;
import com.leadmanager.model.LeadStatus;
import com.leadmanager.repository.LeadRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LeadService {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private EmailService emailService;

    public Lead createLead(Lead lead) {

        Lead savedLead = leadRepository.save(lead);

        // Send auto-reply email
        try {

            emailService.sendAutoReply(savedLead);

        } catch (Exception e) {

            System.err.println(
                "Failed to send auto-reply: "
                + e.getMessage()
            );
        }

        return savedLead;
    }

    public List<Lead> getAllLeads() {
        return leadRepository.findAll();
    }

    public Optional<Lead> getLeadById(Long id) {
        return leadRepository.findById(id);
    }

    public List<Lead> getLeadsByStatus(LeadStatus status) {
        return leadRepository.findByStatus(status);
    }

    public List<Lead> searchLeads(String keyword) {
        return leadRepository.searchLeads(keyword);
    }

    public Lead updateLeadStatus(
            Long id,
            LeadStatus newStatus) {

        Optional<Lead> leadOpt =
            leadRepository.findById(id);

        if (leadOpt.isPresent()) {

            Lead lead = leadOpt.get();

            lead.setStatus(newStatus);

            return leadRepository.save(lead);
        }

        return null;
    }

    public void deleteLead(Long id) {
        leadRepository.deleteById(id);
    }
}