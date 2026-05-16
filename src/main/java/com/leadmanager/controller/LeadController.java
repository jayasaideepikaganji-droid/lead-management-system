package com.leadmanager.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.leadmanager.model.Lead;
import com.leadmanager.model.LeadStatus;
import com.leadmanager.service.LeadService;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin(origins = "*")
public class LeadController {

	@Autowired
	private LeadService leadService;

	@PostMapping
	public ResponseEntity<Map<String, Object>> createLead(@Valid @RequestBody Lead lead) {

	    Map<String, Object> response = new HashMap<>();

	    try {

	        Lead savedLead = leadService.createLead(lead);

	        response.put("success", true);

	        response.put(
	                "message",
	                "Lead submitted successfully"
	        );

	        response.put("data", savedLead);

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {

	        response.put("success", false);

	        response.put(
	                "message",
	                e.getMessage()
	        );

	        return ResponseEntity
	                .badRequest()
	                .body(response);
	    }
	}

	@GetMapping
	public ResponseEntity<List<Lead>> getAllLeads() {
		List<Lead> leads = leadService.getAllLeads();
		return ResponseEntity.ok(leads);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Lead> getLeadById(@PathVariable Long id) {
		return leadService.getLeadById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@GetMapping("/search")
	public ResponseEntity<List<Lead>> searchLeads(@RequestParam String keyword) {
		List<Lead> leads = leadService.searchLeads(keyword);
		return ResponseEntity.ok(leads);
	}

	@GetMapping("/status/{status}")
	public ResponseEntity<List<Lead>> getLeadsByStatus(@PathVariable LeadStatus status) {
		List<Lead> leads = leadService.getLeadsByStatus(status);
		return ResponseEntity.ok(leads);
	}

	@PutMapping("/{id}/status")
	public ResponseEntity<?> updateLeadStatus(
	        @PathVariable Long id,
	        @RequestBody Map<String, String> statusUpdate) {

	    try {

	        String statusValue = statusUpdate.get("status");

	        if (statusValue == null || statusValue.trim().isEmpty()) {

	            Map<String, Object> errorResponse = new HashMap<>();
	            errorResponse.put("success", false);
	            errorResponse.put("message", "Status is required");

	            return ResponseEntity
	                    .status(HttpStatus.BAD_REQUEST)
	                    .body(errorResponse);
	        }

	        LeadStatus newStatus =
	                LeadStatus.valueOf(statusValue.toUpperCase());

	        Lead updatedLead =
	                leadService.updateLeadStatus(id, newStatus);

	        if (updatedLead != null) {

	            Map<String, Object> response =
	                    new HashMap<>();

	            response.put("success", true);
	            response.put("message", "Status updated successfully");
	            response.put("lead", updatedLead);

	            return ResponseEntity.ok(response);

	        } else {

	            Map<String, Object> errorResponse =
	                    new HashMap<>();

	            errorResponse.put("success", false);
	            errorResponse.put("message", "Lead not found");

	            return ResponseEntity
	                    .status(HttpStatus.NOT_FOUND)
	                    .body(errorResponse);
	        }

	    } catch (IllegalArgumentException e) {

	        Map<String, Object> errorResponse =
	                new HashMap<>();

	        errorResponse.put("success", false);
	        errorResponse.put("message", "Invalid status value");

	        return ResponseEntity
	                .status(HttpStatus.BAD_REQUEST)
	                .body(errorResponse);

	    } catch (Exception e) {

	        Map<String, Object> errorResponse =
	                new HashMap<>();

	        errorResponse.put("success", false);
	        errorResponse.put("message",
	                "Error updating status: " + e.getMessage());

	        return ResponseEntity
	                .status(HttpStatus.BAD_REQUEST)
	                .body(errorResponse);
	    }
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteLead(@PathVariable Long id) {

	try {

	leadService.deleteLead(id);

	

	Map<String, Object> response = new HashMap<>();

	response.put("success", true);

	response.put("message", "Lead deleted successfully");

	return ResponseEntity.ok (response);

	} catch (Exception e) {

	Map<String, Object> errorResponse= new HashMap<>();

	errorResponse.put("success", false);

	errorResponse.put("message", "Error deleting lead: "+e.getMessage());

	return ResponseEntity.status(HttpStatus. BAD_REQUEST).body (errorResponse);

	}
}}