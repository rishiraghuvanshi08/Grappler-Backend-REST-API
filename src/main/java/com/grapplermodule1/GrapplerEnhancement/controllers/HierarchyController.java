package com.grapplermodule1.GrapplerEnhancement.controllers;

import com.grapplermodule1.GrapplerEnhancement.customexception.*;
import com.grapplermodule1.GrapplerEnhancement.dtos.HierarchyDTO;
import com.grapplermodule1.GrapplerEnhancement.dtos.ImmediateHierarchyDTO;
import com.grapplermodule1.GrapplerEnhancement.dtos.TeamMembersDTO;
import com.grapplermodule1.GrapplerEnhancement.entities.HierarchyUpdate;
import com.grapplermodule1.GrapplerEnhancement.service.HierarchyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

//@(value = "Hierarchy Management", tags = "Hierarchy Management")
@RestController
@RequestMapping("/hierarchy")
public class HierarchyController {

    private static final Logger log = LoggerFactory.getLogger(HierarchyController.class);

    @Autowired
    private HierarchyService hierarchyService;

    /**
     * For Reporting Hierarchy From Head (CEO)
     * 
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reporting")
    public ResponseEntity<?> getReportingHierarchy() {
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Get Reporting Hierarchy API Called, UUID {}", debugUuid);
            HierarchyDTO hierarchyDTO = hierarchyService.getReportingHierarchy();
            return new ResponseEntity<>(hierarchyDTO, HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            log.error("UUID {} UserNotFoundException In Get Reporting Hierarchy API Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            log.error("UUID {} Exception In Get Reporting Hierarchy API Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For Immediate Reporing Hierarchy
     *
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/immediate-reporting/{userId}")
    public ResponseEntity<?> getImmediateReportingHierarchyById(@Valid @PathVariable("userId") Long userId) {
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Get Immediate Reporting Hierarchy By Id API Called, UUID {}", debugUuid);
            ImmediateHierarchyDTO hierarchyDTO = hierarchyService.getImmediateReportingHierarchy(userId);

            return new ResponseEntity<>(hierarchyDTO, HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            log.error("UUID {} UserNotFoundException In Get Immediate Reporting Hierarchy By Id API Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            log.error("UUID {} Exception In Get Immediate Reporting Hierarchy By Id API Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For Reporting Hierarchy From Particular Users
     * 
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reporting/{userId}")
    public ResponseEntity<?> getReportingHierarchyById(@Valid @PathVariable("userId") Long userId) {
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Get Reporting Hierarchy By Id API Called, UUID {}", debugUuid);
            HierarchyDTO hierarchyDTO = hierarchyService.getReportingHierarchyById(userId);

            return new ResponseEntity<>(hierarchyDTO, HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            log.error("UUID {} UserNotFoundException In Get Reporting Hierarchy By Id API Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            log.error("UUID {} Exception In Get Reporting Hierarchy By Id API Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For Team Hierarchy By TeamId
     * 
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/team/{teamId}")
    public ResponseEntity<?> getTeamHierarchy(@Valid @PathVariable("teamId") Long teamId) {
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Get Team Hierarchy By Id API Called, UUID {}", debugUuid);
            List<TeamMembersDTO> optionalListOfUsers = hierarchyService.getTeamHierarchyById(teamId);

            return new ResponseEntity<>(optionalListOfUsers, HttpStatus.OK);
        }
        catch (TeamMembersNotFoundException e) {
            log.error("UUID {} TeamMembersNotFoundException In Get Team By Id API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (TeamNotFoundException e) {
            log.error("UUID {} TeamNotFoundException In Get Team By Id API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            log.error("UUID {} Exception In Get Team By Id API Exception {} ", debugUuid, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For Hierarchy Updation
     * 
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/update-reporting-hierarchy")
    public ResponseEntity<?> updateHierarchy(@Valid @RequestBody HierarchyUpdate hierarchyUpdate) {
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Update Reporting Hierarchy API Called, UUID {}", debugUuid);
            Boolean updated = hierarchyService.updateHierarchy(hierarchyUpdate);

            return new ResponseEntity<>(new CustomResponseMessage(updated, "Hierary Updated Successfully."), HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            log.error("UUID {} UserNotFoundException In Update Hierarchy API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            log.error("UUID {} Exception In Update Hierarchy API Exception {} ", debugUuid, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

