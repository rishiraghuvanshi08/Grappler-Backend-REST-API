package com.grapplermodule1.GrapplerEnhancement.service;

import com.grapplermodule1.GrapplerEnhancement.customexception.TeamMembersNotFoundException;
import com.grapplermodule1.GrapplerEnhancement.customexception.TeamNotFoundException;
import com.grapplermodule1.GrapplerEnhancement.customexception.UserNotFoundException;
import com.grapplermodule1.GrapplerEnhancement.dtos.HierarchyDTO;
import com.grapplermodule1.GrapplerEnhancement.dtos.ImmediateHierarchyDTO;
import com.grapplermodule1.GrapplerEnhancement.dtos.TeamMembersDTO;
import com.grapplermodule1.GrapplerEnhancement.dtos.UsersDTO;
import com.grapplermodule1.GrapplerEnhancement.entities.HierarchyUpdate;
import com.grapplermodule1.GrapplerEnhancement.entities.Team;
import com.grapplermodule1.GrapplerEnhancement.entities.Users;
import com.grapplermodule1.GrapplerEnhancement.repository.TeamMemberRepository;
import com.grapplermodule1.GrapplerEnhancement.repository.TeamRepository;
import com.grapplermodule1.GrapplerEnhancement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class HierarchyService {

    private static final Logger log = LoggerFactory.getLogger(HierarchyService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private TeamRepository teamRepository;

    /**
     * For Getting Reporting Hierarchy From Top Level
     *
     * @return HierarchyDTO
     */
    public HierarchyDTO getReportingHierarchy(){
        final String designation = "CEO";
        try {
            Optional<Users> optionalUser = userRepository.findByDesignation(designation);
            if(optionalUser.isPresent()){
                log.info("Get Reporting Hierarchy Service Called");

                Users user = optionalUser.get();

                HierarchyDTO hierarchyDTO = new HierarchyDTO(user.getId(), user.getName(), user.getDesignation());
                List<HierarchyDTO> hierarchyDTOList = new ArrayList<>();

                for (Users eachUser : user.getSubordinates()) {
                    HierarchyDTO dto = convertToDTO(eachUser);
                    hierarchyDTOList.add(dto);
                }

                hierarchyDTO.setSubordinates(hierarchyDTOList);
                log.info("Get Reporting Hierarchy Service returning HierarchyDTO");
                return hierarchyDTO;
            }
            else {
                log.error("Get Reporting Hierarchy Service Call UserNotFoundException");
                throw new UserNotFoundException("User Not Found With Designation : CEO");
            }
        }
        catch (Exception e) {
            log.error("Exception In Get Reporting Hierarchy Exception {}", e.getMessage());
            throw e;
        }
    }

    /**
     * For Getting Reporting Hierarchy For Specified Level
     *
     * @return HierarchyDTO
     */
    public HierarchyDTO getReportingHierarchyById(Long userId){
        try {
            Optional<Users> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                log.info("Get Reporting Hierarchy By Id Service Called");
                Users user = optionalUser.get();

                HierarchyDTO hierarchyDTO = new HierarchyDTO(user.getId(), user.getName(), user.getDesignation());
                List<HierarchyDTO> hierarchyDTOList = new ArrayList<>();

                for (Users eachUser : user.getSubordinates()) {
                    HierarchyDTO dto = convertToDTO(eachUser);
                    hierarchyDTOList.add(dto);
                }

                hierarchyDTO.setSubordinates(hierarchyDTOList);
                log.info("Get Reporting Hierarchy By Id Service returning HierarchyDTO");
                return hierarchyDTO;
            } else {
                log.error("Get Reporting Hierarchy By Id Service Call UserNotFoundException");
                throw new UserNotFoundException("User Not Found With Id: " + userId);
            }
        }
        catch (Exception e) {
            log.error("Exception In Get Reporting Hierarchy By Id Exception {}", e.getMessage());
            throw e;
        }
    }

    /**
     * For Converting User To DTO
     *
     * @return HierarchyDTO
     */
    private HierarchyDTO convertToDTO(Users user) {
        try {
            HierarchyDTO dto = new HierarchyDTO(user.getId(), user.getName(), user.getDesignation());

            List<Users> subordinates = user.getSubordinates();
            if (subordinates != null && !subordinates.isEmpty()) {
                for (Users subordinate : subordinates) {
                    HierarchyDTO subordinateDTO = convertToDTO(subordinate);
                    dto.getSubordinates().add(subordinateDTO);
                }
            }
            return dto;
        }
        catch (Exception e) {
            log.error("Exception In Convert To DTO Exception {}", e.getMessage());
            throw e;
        }
    }

    /**
     * For Immediate Hierarchy By User ID
     *
     * @return ImmediateHierarchyDTO
     */
    public ImmediateHierarchyDTO getImmediateReportingHierarchy(Long userId) {
        try {
            Optional<Users> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                log.info("Get Immediate Reporting Hierarchy By Id Service Called");
                Users user = optionalUser.get();

                ImmediateHierarchyDTO immediateHierarchyDTO = new ImmediateHierarchyDTO(user.getId(), user.getName(), user.getDesignation());
                List<UsersDTO> hierarchyDTOList = new ArrayList<>();

                Optional<List<UsersDTO>> optionalUsersDTOS = userRepository.findAllByReportingId(userId);
                if(optionalUsersDTOS.isPresent() && !optionalUsersDTOS.get().isEmpty()) {
                    hierarchyDTOList.addAll(optionalUsersDTOS.get());

                    immediateHierarchyDTO.setSubordinates(optionalUsersDTOS.get());

                    log.info("Get Immediate Reporting Hierarchy By Id Service returning ImmediateHierarchyDTO");
                    return immediateHierarchyDTO;
                }
                else {
                    log.error("Get Immediate Reporting Hierarchy By Id Service Call UserNotFoundException");
                    return immediateHierarchyDTO;
                }
            } else {
                log.error("Get Immediate Reporting Hierarchy By Id Service Call UserNotFoundException");
                throw new UserNotFoundException("User Not Found With Id: " + userId);
            }
        }
        catch (Exception e) {
            log.error("Exception In Get Immediate Reporting Hierarchy By Id Exception {}", e.getMessage());
            throw e;
        }
    }

    /**
     * For Getting Team Hierarchy By Id
     *
     * @return List<TeamMembersDTO>
     */
    public List<TeamMembersDTO> getTeamHierarchyById(Long teamId){
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Get Team Hierarchy By Id Service Called");

            Optional<Team> optionalTeam = teamRepository.findById(teamId);
            if (optionalTeam.isPresent()){
                Optional<List<TeamMembersDTO>> optionalListOfMembers = teamMemberRepository.findUsersByTeamId(teamId);

                if(optionalListOfMembers.isPresent() && !optionalListOfMembers.get().isEmpty()){
                    log.info("Get Team Hierarchy By Id Service returning List of TeamMemberDTO");
                    return optionalListOfMembers.get();
                }
                else {
                    log.error("Get Team Hierarchy By Id Service Call TeamMemberNotFoundException");
                    throw new TeamMembersNotFoundException("Team Members Not Found, Team Id : " + teamId);
                }
            }
            else {
                log.error("Get Team Hierarchy By Id Service Call TeamNotFoundException");
                throw new TeamNotFoundException("Team Not Found With Team Id : " + teamId);
            }
        }
        catch (Exception e) {
            log.error("Exception In Get Team Hierarchy By Id Service Exception {}", e.getMessage());
            throw e;
        }
    }

    public Boolean updateHierarchy(HierarchyUpdate hierarchyUpdate) {
        try {
            log.info("Update Hierarchy Service Called");

            Optional<Users> optionalUserDragged = userRepository.findById(hierarchyUpdate.getDraggedId());
            Optional<Users> optionalUserDropped = userRepository.findById(hierarchyUpdate.getDroppedId());
            if (optionalUserDragged.isPresent() && optionalUserDropped.isPresent()){
                Users draggedUser = optionalUserDragged.get();

                draggedUser.setReportingUser(optionalUserDropped.get());

                Users droppedUser = optionalUserDropped.get();
                List<Users> subordinates = droppedUser.getSubordinates();
                subordinates.add(draggedUser);
                droppedUser.setSubordinates(subordinates);

                userRepository.save(droppedUser);
                return true;
            }
            else {
                log.error("Update Hierarchy Service Call UserNotFoundException");
                throw new TeamNotFoundException("Users Not Found");
            }
        }
        catch (Exception e) {
            log.error("Exception In Update Hierarchy Service Exception {}", e.getMessage());
            throw e;
        }
    }
}
