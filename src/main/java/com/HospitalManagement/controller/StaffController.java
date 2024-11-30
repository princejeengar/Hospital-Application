package com.HospitalManagement.controller;

import com.HospitalManagement.dto.StaffDTO;
import com.HospitalManagement.service.StaffService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/staff")
@Tag(name = "Staff APIs", description = "Create, Get, GetAll, Update & Delete")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @PostMapping("/create")
    public ResponseEntity<StaffDTO> create(@RequestBody StaffDTO staffDTO) {
        StaffDTO createdStaff = staffService.create(staffDTO);
        return ResponseEntity.ok(createdStaff);
    }

    @GetMapping("/get-staff/{id}")
    public ResponseEntity<StaffDTO> getById(@PathVariable Long id) {
        StaffDTO staffDTO = staffService.getById(id);
        return ResponseEntity.ok(staffDTO);
    }

    @GetMapping("/get-all-staff")
    public ResponseEntity<List<StaffDTO>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        List<StaffDTO> staffDTOs = staffService.getAll(pageNumber, pageSize);
        return ResponseEntity.ok(staffDTOs);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<StaffDTO> update(@PathVariable Long id, @RequestBody StaffDTO staffDTO) {
        StaffDTO updatedStaff = staffService.update(id, staffDTO);
        return ResponseEntity.ok(updatedStaff);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        staffService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-paginated")
    public ResponseEntity<Page<StaffDTO>> searchByDepartmentWithPagination(
            @RequestParam(value = "department", required = false) String department,
            @RequestParam(value = "page", defaultValue = "0") int pageNumber,
            @RequestParam(value = "size", defaultValue = "5") int pageSize) {

        Page<StaffDTO> paginatedStaff = staffService.searchByDepartmentWithPagination(department, pageNumber, pageSize);
        return ResponseEntity.ok(paginatedStaff);
    }
}