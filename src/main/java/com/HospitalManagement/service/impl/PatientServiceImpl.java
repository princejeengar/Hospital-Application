package com.HospitalManagement.service.impl;

import com.HospitalManagement.dto.AppUserDTO;
import com.HospitalManagement.dto.HospitalDTO;
import com.HospitalManagement.dto.PatientDTO;
import com.HospitalManagement.entities.Hospital;
import com.HospitalManagement.entities.Patient;
import com.HospitalManagement.entities.AppUser;
import com.HospitalManagement.exception.ResourceNotFoundException;
import com.HospitalManagement.repository.HospitalRepo;
import com.HospitalManagement.repository.PatientRepo;
import com.HospitalManagement.repository.AppUserRepo;
import com.HospitalManagement.service.PatientService;
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
public class PatientServiceImpl implements PatientService {

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private HospitalRepo hospitalRepo;

    @Autowired
    private AppUserRepo appUserRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public PatientDTO create(PatientDTO patientDTO) {
        Hospital hospital = hospitalRepo.findById(patientDTO.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found"));
        AppUser appUser = appUserRepo.findById(patientDTO.getAppUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Patient patient = new Patient();
        patient.setHospital(hospital);
        patient.setAppUser(appUser);
        patient.setDisease(patientDTO.getDisease());
        patient.setStatus(patientDTO.getStatus());

        Patient savedPatient = patientRepo.save(patient);
        return mapToDTO(savedPatient);
    }

    @Override
    public PatientDTO getById(Long id) {
        Patient patient = patientRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        return mapToDTO(patient);
    }

    @Override
    public List<PatientDTO> getAll(Integer pageNumber, Integer pageSize) {
        return patientRepo.findAll(PageRequest.of(pageNumber, pageSize))
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PatientDTO update(Long id, PatientDTO patientDTO) {
        Patient patient = patientRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));

        if (patientDTO.getHospitalId() != null) {
            Hospital hospital = hospitalRepo.findById(patientDTO.getHospitalId())
                    .orElseThrow(() -> new RuntimeException("Hospital not found"));
            patient.setHospital(hospital);
        }

        if (patientDTO.getAppUserId() != null) {
            AppUser appUser = appUserRepo.findById(patientDTO.getAppUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            patient.setAppUser(appUser);
        }

        patient.setDisease(patientDTO.getDisease());
        patient.setStatus(patientDTO.getStatus());

        Patient updatedPatient = patientRepo.save(patient);
        return mapToDTO(updatedPatient);
    }

    @Override
    public void delete(Long id) {
        Patient patient = patientRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
        patientRepo.delete(patient);
    }

    @Override
    public Page<PatientDTO> searchByDiseaseWithPagination(String disease, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Patient> query = cb.createQuery(Patient.class);
        Root<Patient> root = query.from(Patient.class);
        Predicate predicate = cb.conjunction();
        if (disease != null && !disease.trim().isEmpty()) {
            predicate = cb.like(cb.lower(root.get("disease")), "%" + disease.trim().toLowerCase() + "%");
        }
        query.where(predicate);
        TypedQuery<Patient> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Patient> patients = typedQuery.getResultList();
        long totalRecords = entityManager.createQuery(query).getResultList().size();
        List<PatientDTO> patientDTOs = patients.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(patientDTOs, pageable, totalRecords);
    }

    public PatientDTO mapToDTO(Patient patient) {
        PatientDTO patientDTO = new PatientDTO();
        patientDTO.setId(patient.getId());
        patientDTO.setDisease(patient.getDisease());
        patientDTO.setStatus(patient.getStatus());

        if (patient.getHospital() != null) {
            patientDTO.setHospitalId(patient.getHospital().getId());
            HospitalDTO hospitalDTO = new HospitalDTO();
            hospitalDTO.setId(patient.getHospital().getId());
            hospitalDTO.setName(patient.getHospital().getName());
            hospitalDTO.setAddress(patient.getHospital().getAddress());
            hospitalDTO.setContactNo(patient.getHospital().getContactNo());
            patientDTO.setHospital(hospitalDTO);
        }

        if (patient.getAppUser() != null) {
            patientDTO.setAppUserId(patient.getAppUser().getId());
            AppUserDTO appUserDTO = new AppUserDTO();
            appUserDTO.setId(patient.getAppUser().getId());
            appUserDTO.setUsername(patient.getAppUser().getUsername());
            appUserDTO.setEmail(patient.getAppUser().getEmail());
            appUserDTO.setPassword(patient.getAppUser().getPassword());
            appUserDTO.setName(patient.getAppUser().getName());
            appUserDTO.setContactNo(patient.getAppUser().getContactNo());
            appUserDTO.setRole(patient.getAppUser().getRole());
            patientDTO.setAppUser(appUserDTO);
        }
        return patientDTO;
    }
}