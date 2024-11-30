package com.HospitalManagement.service;

import com.HospitalManagement.dto.HospitalDTO;
import com.HospitalManagement.entities.Hospital;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HospitalService {
    HospitalDTO create(HospitalDTO hospitalDTO);
    HospitalDTO getById(Long id);
    List<HospitalDTO> getAll(Integer pageNumber, Integer pageSize);
    HospitalDTO update(Long id, HospitalDTO hospitalDTO);
    void delete(Long id);
    Page<HospitalDTO> searchByUsernameWithPagination(String name, Integer pageNumber, Integer pageSize);
}