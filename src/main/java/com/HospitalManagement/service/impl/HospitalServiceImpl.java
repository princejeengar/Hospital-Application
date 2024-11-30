package com.HospitalManagement.service.impl;

import com.HospitalManagement.dto.HospitalDTO;
import com.HospitalManagement.entities.AppUser;
import com.HospitalManagement.entities.Hospital;
import com.HospitalManagement.repository.HospitalRepo;
import com.HospitalManagement.service.HospitalService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepo hospitalRepo;

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public HospitalDTO create(HospitalDTO hospitalDTO) {
        Hospital hospital = mapToEntity(hospitalDTO);
        Hospital savedHospital = hospitalRepo.save(hospital);
        return mapToDTO(savedHospital);
    }

    @Override
    public HospitalDTO getById(Long id) {
        Hospital hospital = hospitalRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found with id: " + id));
        return mapToDTO(hospital);
    }

    @Override
    public List<HospitalDTO> getAll(Integer pageNumber, Integer pageSize) {
        return hospitalRepo.findAll(PageRequest.of(pageNumber, pageSize))
                .getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public HospitalDTO update(Long id, HospitalDTO hospitalDTO) {
        Hospital existingHospital = hospitalRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found with id: " + id));

        existingHospital.setName(hospitalDTO.getName());
        existingHospital.setAddress(hospitalDTO.getAddress());
        existingHospital.setContactNo(hospitalDTO.getContactNo());

        Hospital updatedHospital = hospitalRepo.save(existingHospital);
        return mapToDTO(updatedHospital);
    }

    @Override
    public void delete(Long id) {
        hospitalRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital not found with id: " + id));
        hospitalRepo.deleteById(id);
    }

    @Override
    public Page<HospitalDTO> searchByUsernameWithPagination(String name, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Hospital> query = cb.createQuery(Hospital.class);
        Root<Hospital> root = query.from(Hospital.class);
        Predicate predicate = cb.conjunction();
        if (name != null && !name.trim().isEmpty()) {
            predicate = cb.like(cb.lower(root.get("name")), "%" + name.trim().toLowerCase() + "%");
        }
        query.where(predicate);
        TypedQuery<Hospital> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Hospital> hospitals = typedQuery.getResultList();
        long totalRecords = entityManager.createQuery(query).getResultList().size();
        List<HospitalDTO> hospitalDTOs = hospitals.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(hospitalDTOs, pageable, totalRecords);
    }


    // Convert Hospital Entity to DTO
    private HospitalDTO mapToDTO(Hospital hospital) {
        HospitalDTO hospitalDTO = new HospitalDTO();
        hospitalDTO.setId(hospital.getId());
        hospitalDTO.setName(hospital.getName());
        hospitalDTO.setAddress(hospital.getAddress());
        hospitalDTO.setContactNo(hospital.getContactNo());
        return hospitalDTO;
    }

    // Convert DTO to Hospital Entity
    private Hospital mapToEntity(HospitalDTO hospitalDTO) {
        Hospital hospital = new Hospital();
        hospital.setName(hospitalDTO.getName());
        hospital.setAddress(hospitalDTO.getAddress());
        hospital.setContactNo(hospitalDTO.getContactNo());
        return hospital;
    }
}