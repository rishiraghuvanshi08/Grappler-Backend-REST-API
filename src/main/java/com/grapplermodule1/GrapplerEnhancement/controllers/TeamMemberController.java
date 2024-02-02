package com.grapplermodule1.GrapplerEnhancement.controllers;

import com.grapplermodule1.GrapplerEnhancement.customexception.*;
import com.grapplermodule1.GrapplerEnhancement.dtos.TeamMembersDTO;
import com.grapplermodule1.GrapplerEnhancement.dtos.UsersDTO;
import com.grapplermodule1.GrapplerEnhancement.entities.Permission;
import com.grapplermodule1.GrapplerEnhancement.entities.TeamMembers;
import com.grapplermodule1.GrapplerEnhancement.entities.Users;
import com.grapplermodule1.GrapplerEnhancement.service.HierarchyService;
import com.grapplermodule1.GrapplerEnhancement.service.TeamMembersService;
import com.grapplermodule1.GrapplerEnhancement.service.TeamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/team-members")
public class TeamMemberController {

    private static final Logger log = LoggerFactory.getLogger(HierarchyController.class);

    @Autowired
    private HierarchyService hierarchyService;

    @Autowired
    private TeamMembersService teamMembersService;

    /**
     * For Getting Team Members By Team ID
     *
     * @return ResponseEntity<?>
     */
    // @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{teamId}")
    public ResponseEntity<?> getTeamMembers(@PathVariable("teamId") Long teamId){
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
     * For Getting Team Member Details
     * 
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{teamId}/member/{userId}")
    public ResponseEntity<?> getTeamMemberDetails(@PathVariable("teamId") Long teamId, @PathVariable("userId") Long userId){
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Get Team Member Details API Called, UUID {}", debugUuid);
            UsersDTO userDTO = teamMembersService.getMemberDetail(teamId, userId);

            return new ResponseEntity<>(userDTO, HttpStatus.OK);
        }
        catch (MemberNotPresentInTeamException e) {
            log.error("UUID {} MemberNotPresentInTeamException In Add New Member API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (UserNotFoundException e) {
            log.error("UUID {} UserNotFoundException In Add New Member API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (TeamNotFoundException e) {
            log.error("UUID {} TeamNotFoundException In Add New Member API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            log.error("UUID {} Exception In Add New Member API, Exception {} ", debugUuid, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For Adding New Member In Team With Team ID and User ID
     *
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{teamId}/add-new-members")
    public ResponseEntity<?> addNewMember(@PathVariable("teamId") Long teamId, @RequestBody List<Long> userIds){
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Add New Member API Called, UUID {}", debugUuid);
            List<TeamMembers> teamMembers = teamMembersService.addNewMembers(teamId, userIds);

            return new ResponseEntity<>(new CustomResponseMessage(true, "New Members Added Successfully."), HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            log.error("UUID {} UserNotFoundException In Add New Member API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (TeamNotFoundException e) {
            log.error("UUID {} TeamNotFoundException In Add New Member API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (MemberAlreadyPresentException e) {
            log.error("UUID {} MemberAlreadyPresentException In Add New Member API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponseMessage(false, e.getMessage()), HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            log.error("UUID {} Exception In Add New Member API, Exception {} ", debugUuid, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For Delete Member By Team ID and User ID
     *
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{teamId}/delete-member/{userId}")
    public ResponseEntity<?> deleteMember(@PathVariable("teamId") Long teamId,
                                       @PathVariable("userId") Long userId){
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Get Team Member Details API Called, UUID {}", debugUuid);
            Boolean isDeleted = teamMembersService.deleteTeamMember(teamId, userId);

            return new ResponseEntity<>(new CustomResponseMessage(isDeleted, "Member Is Deleted Successfully."), HttpStatus.OK);
        }
        catch (MemberNotPresentInTeamException e) {
            log.error("UUID {} MemberNotPresentInTeamException In Delete Member API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (UserNotFoundException e) {
            log.error("UUID {} UserNotFoundException In Add Delete API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (TeamNotFoundException e) {
            log.error("UUID {} TeamNotFoundException In Delete Member API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            log.error("UUID {} Exception In Delete Member API, Exception {} ", debugUuid, e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
