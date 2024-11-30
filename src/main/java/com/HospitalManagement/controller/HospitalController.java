package com.HospitalManagement.controller;

import com.HospitalManagement.dto.HospitalDTO;
import com.HospitalManagement.service.HospitalService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hospital")
@Tag(name = "Hospital APIs", description = "Create, Get, GetAll, Update & Delete")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @PostMapping("/create")
    public ResponseEntity<HospitalDTO> create(@RequestBody HospitalDTO hospitalDTO) {
        HospitalDTO createdHospital = hospitalService.create(hospitalDTO);
        return ResponseEntity.ok(createdHospital);
    }

    @GetMapping("get-hospital/{id}")
    public ResponseEntity<HospitalDTO> getById(@PathVariable Long id) {
        HospitalDTO hospitalDTO = hospitalService.getById(id);
        return ResponseEntity.ok(hospitalDTO);
    }

    @GetMapping("/get-all-hospitals")
    public ResponseEntity<List<HospitalDTO>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        List<HospitalDTO> hospitals = hospitalService.getAll(pageNumber, pageSize);
        return ResponseEntity.ok(hospitals);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<HospitalDTO> update(@PathVariable Long id, @RequestBody HospitalDTO hospitalDTO) {
        HospitalDTO updatedHospital = hospitalService.update(id, hospitalDTO);
        return ResponseEntity.ok(updatedHospital);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hospitalService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-paginated")
    public ResponseEntity<Page<HospitalDTO>> searchByUsernameWithPagination(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "page", defaultValue = "0") int pageNumber,
            @RequestParam(value = "size", defaultValue = "5") int pageSize) {

        Page<HospitalDTO> paginatedUsers = hospitalService.searchByUsernameWithPagination(name, pageNumber, pageSize);
        return ResponseEntity.ok(paginatedUsers);
    }
}