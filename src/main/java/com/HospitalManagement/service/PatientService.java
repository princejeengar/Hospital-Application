package com.HospitalManagement.service;

import com.HospitalManagement.dto.PatientDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PatientService {
    PatientDTO create(PatientDTO patientDTO);
    PatientDTO getById(Long id);
    List<PatientDTO> getAll(Integer pageNumber, Integer pageSize);
    PatientDTO update(Long id, PatientDTO patientDTO);
    void delete(Long id);
    Page<PatientDTO> searchByDiseaseWithPagination(String disease, Integer pageNumber, Integer pageSize);

}