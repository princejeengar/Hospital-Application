package com.HospitalManagement.repository;

import com.HospitalManagement.entities.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HospitalRepo extends JpaRepository<Hospital, Long> {
}