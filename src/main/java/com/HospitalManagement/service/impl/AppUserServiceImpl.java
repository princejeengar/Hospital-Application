package com.HospitalManagement.service.impl;

import com.HospitalManagement.dto.AppUserDTO;
import com.HospitalManagement.entities.AppUser;
import com.HospitalManagement.repository.AppUserRepo;
import com.HospitalManagement.service.AppUserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private AppUserRepo appUserRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public AppUserDTO create(AppUserDTO appUserDTO) {
        AppUser appUser = mapToEntity(appUserDTO);
        AppUser savedAppUser = appUserRepo.save(appUser);
        return mapToDTO(savedAppUser);
    }

    @Override
    public AppUserDTO getById(Long id) {
        AppUser appUser = appUserRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToDTO(appUser);
    }

    @Override
    public List<AppUserDTO> getAll(Integer pageNumber, Integer pageSize) {
        return appUserRepo.findAll(PageRequest.of(pageNumber, pageSize))
                .getContent().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public AppUserDTO update(Long id, AppUserDTO appUserDTO) {
        AppUser existingAppUser = appUserRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        existingAppUser.setEmail(appUserDTO.getEmail());
        existingAppUser.setUsername(appUserDTO.getUsername());
        existingAppUser.setPassword(appUserDTO.getPassword());
        existingAppUser.setName(appUserDTO.getName());
        existingAppUser.setContactNo(appUserDTO.getContactNo());
        existingAppUser.setRole(appUserDTO.getRole());

        return mapToDTO(appUserRepo.save(existingAppUser));
    }

    @Override
    public void deleteById(Long id) {
        if (!appUserRepo.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        appUserRepo.deleteById(id);
    }

    @Override
    public Page<AppUser> searchByUsernameWithPagination(String username, Integer pageNumber, Integer pageSize) {
        if (username != null) {
            username = username.replaceAll("\\s+", " ");
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AppUser> query = cb.createQuery(AppUser.class);
        Root<AppUser> root = query.from(AppUser.class);
        Predicate predicate = cb.conjunction();
        if (username != null && !username.trim().isEmpty()) {
            predicate = cb.like(cb.lower(root.get("username")), "%" + username.trim().toLowerCase() + "%");
        }
        query.where(predicate);
        TypedQuery<AppUser> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<AppUser> appUsers = typedQuery.getResultList();
        long totalRecords = appUsers.size();

        return new PageImpl<>(appUsers, pageable, totalRecords);
    }

    private AppUserDTO mapToDTO(AppUser appUser) {
        AppUserDTO dto = new AppUserDTO();
        dto.setId(appUser.getId());
        dto.setEmail(appUser.getEmail());
        dto.setUsername(appUser.getUsername());
        dto.setPassword(appUser.getPassword());
        dto.setName(appUser.getName());
        dto.setContactNo(appUser.getContactNo());
        dto.setRole(appUser.getRole());
        return dto;
    }

    private AppUser mapToEntity(AppUserDTO appUserDTO) {
        AppUser appUser = new AppUser();
        appUser.setEmail(appUserDTO.getEmail());
        appUser.setUsername(appUserDTO.getUsername());
        appUser.setPassword(appUserDTO.getPassword());
        appUser.setName(appUserDTO.getName());
        appUser.setContactNo(appUserDTO.getContactNo());
        appUser.setRole(appUserDTO.getRole());
        return appUser;
    }
}