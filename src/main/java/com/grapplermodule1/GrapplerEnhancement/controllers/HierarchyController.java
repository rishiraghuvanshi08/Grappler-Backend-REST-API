package com.grapplermodule1.GrapplerEnhancement.controllers;

import com.grapplermodule1.GrapplerEnhancement.customexception.CustomResponse;
import com.grapplermodule1.GrapplerEnhancement.customexception.TeamMembersNotFoundException;
import com.grapplermodule1.GrapplerEnhancement.customexception.TeamNotFoundException;
import com.grapplermodule1.GrapplerEnhancement.customexception.UserNotFoundException;
import com.grapplermodule1.GrapplerEnhancement.dtos.HierarchyDTO;
import com.grapplermodule1.GrapplerEnhancement.dtos.ImmediateHierarchyDTO;
import com.grapplermodule1.GrapplerEnhancement.dtos.TeamMembersDTO;
import com.grapplermodule1.GrapplerEnhancement.service.HierarchyService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    @Operation(summary = "Get Reporting Hierarchy", description = "Returns Reporting Hierarchy From CEO")
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

}

