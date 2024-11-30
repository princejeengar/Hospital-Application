package com.HospitalManagement.controller;

import com.HospitalManagement.dto.PatientDTO;
import com.HospitalManagement.service.PatientService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/patient")
@Tag(name = "Patient APIs", description = "Create, Get, GetAll, Update & Delete")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @PostMapping("/create")
    public ResponseEntity<PatientDTO> create(@RequestBody PatientDTO patientDTO) {
        PatientDTO createdPatient = patientService.create(patientDTO);
        return ResponseEntity.ok(createdPatient);
    }

    @GetMapping("/get-patient/{id}")
    public ResponseEntity<PatientDTO> getById(@PathVariable Long id) {
        PatientDTO patientDTO = patientService.getById(id);
        return ResponseEntity.ok(patientDTO);
    }

    @GetMapping("/get-all-patients")
    public ResponseEntity<List<PatientDTO>> getAll(
            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        List<PatientDTO> patientDTOs = patientService.getAll(pageNumber, pageSize);
        return ResponseEntity.ok(patientDTOs);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<PatientDTO> update(@PathVariable Long id, @RequestBody PatientDTO patientDTO) {
        PatientDTO updatedPatient = patientService.update(id, patientDTO);
        return ResponseEntity.ok(updatedPatient);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-paginated")
    public ResponseEntity<Page<PatientDTO>> searchByDiseaseWithPagination(
            @RequestParam(value = "disease", required = false) String disease,
            @RequestParam(value = "page", defaultValue = "0") int pageNumber,
            @RequestParam(value = "size", defaultValue = "5") int pageSize) {

        Page<PatientDTO> paginatedPatients = patientService.searchByDiseaseWithPagination(disease, pageNumber, pageSize);
        return ResponseEntity.ok(paginatedPatients);
    }

}