package com.HospitalManagement.controller;

import com.HospitalManagement.dto.AppUserDTO;
import com.HospitalManagement.entities.AppUser;
import com.HospitalManagement.service.AppUserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app-user")
@Tag(name = "AppUser APIs", description = "Create, Get, GetAll, Update & Delete")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @PostMapping("/create")
    public ResponseEntity<AppUserDTO> createAppUser(@RequestBody AppUserDTO appUserDTO) {
        AppUserDTO createdAppUser = appUserService.create(appUserDTO);
        return ResponseEntity.ok(createdAppUser);
    }

    @GetMapping("/get-user/{id}")
    public ResponseEntity<AppUserDTO> getAppUserById(@PathVariable Long id) {
        AppUserDTO appUserDTO = appUserService.getById(id);
        return ResponseEntity.ok(appUserDTO);
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<List<AppUserDTO>> getAllAppUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        List<AppUserDTO> appUsers = appUserService.getAll(pageNumber, pageSize);
        return ResponseEntity.ok(appUsers);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AppUserDTO> updateAppUser(@PathVariable Long id, @RequestBody AppUserDTO appUserDTO) {
        AppUserDTO updatedAppUser = appUserService.update(id, appUserDTO);
        return ResponseEntity.ok(updatedAppUser);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteAppUser(@PathVariable Long id) {
        appUserService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-paginated")
    public ResponseEntity<Page<AppUser>> searchByUsernameWithPagination(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "page", defaultValue = "0") int pageNumber,
            @RequestParam(value = "size", defaultValue = "5") int pageSize) {

        Page<AppUser> paginatedUsers = appUserService.searchByUsernameWithPagination(username, pageNumber, pageSize);
        return ResponseEntity.ok(paginatedUsers);
    }
}