package com.HospitalManagement.service;

import com.HospitalManagement.dto.AppUserDTO;
import com.HospitalManagement.entities.AppUser;
import org.springframework.data.domain.Page;
import java.util.List;

public interface AppUserService {
    AppUserDTO create(AppUserDTO appUserDTO);
    AppUserDTO getById(Long id);
    List<AppUserDTO> getAll(Integer pageNumber, Integer pageSize);
    AppUserDTO update(Long id, AppUserDTO appUserDTO);
    void deleteById(Long id);
    Page<AppUser> searchByUsernameWithPagination(String username, Integer pageNumber, Integer pageSize);
}