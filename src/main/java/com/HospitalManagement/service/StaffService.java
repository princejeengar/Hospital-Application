package com.HospitalManagement.service;

import com.HospitalManagement.dto.StaffDTO;
import org.springframework.data.domain.Page;
import java.util.List;

public interface StaffService {
    StaffDTO create(StaffDTO staffDTO);
    StaffDTO getById(Long id);
    List<StaffDTO> getAll(Integer pageNumber, Integer pageSize);
    StaffDTO update(Long id, StaffDTO staffDTO);
    void delete(Long id);
    Page<StaffDTO> searchByDepartmentWithPagination(String department, Integer pageNumber, Integer pageSize);
}