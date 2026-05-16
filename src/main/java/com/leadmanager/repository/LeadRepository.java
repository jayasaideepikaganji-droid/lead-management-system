package com.leadmanager.repository;

import com.leadmanager.model.Lead;
import com.leadmanager.model.LeadStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByStatus(LeadStatus status);

    List<Lead> findByBusinessType(String businessType);

    @Query(
        "SELECT l FROM Lead l WHERE " +
        "LOWER(l.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(l.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(l.businessType) LIKE LOWER(CONCAT('%', :keyword, '%'))"
    )
    List<Lead> searchLeads(@Param("keyword") String keyword);
}