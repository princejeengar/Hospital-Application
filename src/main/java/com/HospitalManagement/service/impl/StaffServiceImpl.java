package com.HospitalManagement.service.impl;

import com.HospitalManagement.dto.AppUserDTO;
import com.HospitalManagement.dto.HospitalDTO;
import com.HospitalManagement.dto.PatientDTO;
import com.HospitalManagement.dto.StaffDTO;
import com.HospitalManagement.entities.AppUser;
import com.HospitalManagement.entities.Hospital;
import com.HospitalManagement.entities.Patient;
import com.HospitalManagement.entities.Staff;
import com.HospitalManagement.repository.AppUserRepo;
import com.HospitalManagement.repository.HospitalRepo;
import com.HospitalManagement.repository.StaffRepo;
import com.HospitalManagement.service.StaffService;
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
public class StaffServiceImpl implements StaffService {

    @Autowired
    private StaffRepo staffRepo;

    @Autowired
    private HospitalRepo hospitalRepo;

    @Autowired
    private AppUserRepo appUserRepo;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public StaffDTO create(StaffDTO staffDTO) {
        // Fetch hospital
        Hospital hospital = hospitalRepo.findById(staffDTO.getHospitalId())
                .orElseThrow(() -> new RuntimeException("Hospital not found"));

        // Fetch AppUser
        AppUser appUser = appUserRepo.findById(staffDTO.getAppUserId())
                .orElseThrow(() -> new RuntimeException("AppUser not found"));

        // Map DTO to Entity
        Staff staff = new Staff();
        staff.setHospital(hospital);
        staff.setAppUser(appUser);
        staff.setDepartment(staffDTO.getDepartment());
        staff.setRole(staffDTO.getRole());

        // Save staff
        Staff savedStaff = staffRepo.save(staff);

        // Map Entity to DTO
        StaffDTO responseDTO = new StaffDTO();
        responseDTO.setId(savedStaff.getId());
        responseDTO.setHospitalId(savedStaff.getHospital().getId());
        responseDTO.setAppUserId(savedStaff.getAppUser().getId());
        responseDTO.setDepartment(savedStaff.getDepartment());
        responseDTO.setRole(savedStaff.getRole());

        return responseDTO;
    }


    @Override
    public StaffDTO getById(Long id) {
        Staff staff = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        return mapToDTO(staff);
    }

    @Override
    public List<StaffDTO> getAll(Integer pageNumber, Integer pageSize) {
        return staffRepo.findAll(PageRequest.of(pageNumber, pageSize))
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StaffDTO update(Long id, StaffDTO staffDTO) {
        // Find existing staff
        Staff existingStaff = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        // Update hospital if necessary
        if (staffDTO.getHospitalId() != null) {
            Hospital hospital = hospitalRepo.findById(staffDTO.getHospitalId())
                    .orElseThrow(() -> new RuntimeException("Hospital not found"));
            existingStaff.setHospital(hospital);
        }

        // Update AppUser if necessary
        if (staffDTO.getAppUserId() != null) {
            AppUser appUser = appUserRepo.findById(staffDTO.getAppUserId())
                    .orElseThrow(() -> new RuntimeException("AppUser not found"));
            existingStaff.setAppUser(appUser);
        }

        // Update other fields
        existingStaff.setDepartment(staffDTO.getDepartment());
        existingStaff.setRole(staffDTO.getRole());

        // Save and map to DTO
        Staff updatedStaff = staffRepo.save(existingStaff);
        StaffDTO responseDTO = new StaffDTO();
        responseDTO.setId(updatedStaff.getId());
        responseDTO.setHospitalId(updatedStaff.getHospital().getId());
        responseDTO.setAppUserId(updatedStaff.getAppUser().getId());
        responseDTO.setDepartment(updatedStaff.getDepartment());
        responseDTO.setRole(updatedStaff.getRole());

        return responseDTO;
    }

    @Override
    public void delete(Long id) {
        // Find existing staff
        Staff existingStaff = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        // Delete staff
        staffRepo.delete(existingStaff);
    }

    @Override
    public Page<StaffDTO> searchByDepartmentWithPagination(String department, Integer pageNumber, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Staff> query = cb.createQuery(Staff.class);
        Root<Staff> root = query.from(Staff.class);
        Predicate predicate = cb.conjunction();
        if (department != null && !department.trim().isEmpty()) {
            predicate = cb.like(cb.lower(root.get("department")), "%" + department.trim().toLowerCase() + "%");
        }
        query.where(predicate);
        TypedQuery<Staff> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());
        List<Staff> staffList = typedQuery.getResultList();
        long totalRecords = entityManager.createQuery(query).getResultList().size();
        List<StaffDTO> staffDTOs = staffList.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(staffDTOs, pageable, totalRecords);
    }

    public StaffDTO mapToDTO(Staff staff) {
        StaffDTO staffDTO = new StaffDTO();
        staffDTO.setId(staff.getId());
        staffDTO.setDepartment(staff.getDepartment());
        staffDTO.setRole(staff.getRole());

        if (staff.getHospital() != null) {
            staffDTO.setHospitalId(staff.getHospital().getId());
            HospitalDTO hospitalDTO = new HospitalDTO();
            hospitalDTO.setId(staff.getHospital().getId());
            hospitalDTO.setName(staff.getHospital().getName());
            hospitalDTO.setAddress(staff.getHospital().getAddress());
            hospitalDTO.setContactNo(staff.getHospital().getContactNo());
            staffDTO.setHospital(hospitalDTO);
        }

        if (staff.getAppUser() != null) {
            staffDTO.setAppUserId(staff.getAppUser().getId());
            AppUserDTO appUserDTO = new AppUserDTO();
            appUserDTO.setId(staff.getAppUser().getId());
            appUserDTO.setUsername(staff.getAppUser().getUsername());
            appUserDTO.setEmail(staff.getAppUser().getEmail());
            appUserDTO.setPassword(staff.getAppUser().getPassword());
            appUserDTO.setName(staff.getAppUser().getName());
            appUserDTO.setContactNo(staff.getAppUser().getContactNo());
            appUserDTO.setRole(staff.getAppUser().getRole());
            staffDTO.setAppUser(appUserDTO);
        }
        return staffDTO;
    }
}