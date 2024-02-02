package com.grapplermodule1.GrapplerEnhancement.controllers;

import com.grapplermodule1.GrapplerEnhancement.customexception.*;
import com.grapplermodule1.GrapplerEnhancement.dtos.ProjectDTO;
import com.grapplermodule1.GrapplerEnhancement.dtos.TeamDTO;
import com.grapplermodule1.GrapplerEnhancement.entities.Team;
import com.grapplermodule1.GrapplerEnhancement.entities.Project;
import com.grapplermodule1.GrapplerEnhancement.service.ProjectService;
import com.grapplermodule1.GrapplerEnhancement.validations.PostValidation;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins="http://localhost:3000/")
public class ProjectsController {

    @Autowired
    private ProjectService projectService;

    private static final Logger log = LoggerFactory.getLogger(ProjectsController.class);

    /**
     * For Getting List of Projects
     *
     * @return ResponseEntity
     **/
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<?> getAllProjects() {
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Getting data of all Projects");
            List<ProjectDTO> projectList = projectService.getAllProjects();
            return new ResponseEntity<>(projectList, HttpStatus.OK);
        } catch (ProjectNotFoundException p) {
            log.error("Getting exception because there is no project in the list, UUID {}", debugUuid, p);
            return new ResponseEntity<>(new CustomResponse<>(false, p.getMessage(), null), HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            log.error("Getting exception when we are trying to fetch the list of the projects, UUID {}", debugUuid, e);
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For Create New Project
     *
     * @return ResponseEntity
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/")
    public ResponseEntity<?> create(@Valid @RequestBody Project project) {
        String debugUuid = UUID.randomUUID().toString();
        try {
            Optional<Project> addProject = Optional.ofNullable(projectService.createProject(project));
            log.info("Add new project and their details with uuid{}", debugUuid);
            if (addProject.isPresent()) {
                log.info("Add new project and their details if project details is present with uuid{}", debugUuid);
                return new ResponseEntity<>(new CustomResponse<>(true, "Project Created With Id : " + addProject.get().getId(),  addProject.get().getId()), HttpStatus.OK);
            } else {
                log.info("UUID {} Project Not Created", debugUuid);
                return new ResponseEntity<>(new CustomResponse<>(false, "Project Not Created. Please Try Again", null), HttpStatus.BAD_GATEWAY);
            }
        }catch (DataNotPresent dnp){
            log.error("UUID {} Getting exception while adding a new project with Exception {}", debugUuid, dnp.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, "Do not Pass Empty String, Please Fill the data Properly", null),HttpStatus.NO_CONTENT);
        }
        catch (DuplicateProjectName dp){
            log.error("UUID {} Getting exception while adding a new project with Exception {}", debugUuid, dp.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, "Project name is already in use", null),HttpStatus.CONFLICT);
        }catch (Exception e) {
            log.error("UUID {} Getting exception while adding a new project with Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, "Project Not Created", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For getting Project By I'd
     *
     * @return ResponseEntity<Project>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{projectId}")
    public ResponseEntity<?> getProjectById(@Valid @PathVariable Long projectId) {
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Inside Get Project By Id,UUID {} ", projectId);
            Optional<ProjectDTO> project = Optional.ofNullable(projectService.getProjectById(projectId));

            log.info("Get project By Id API Called, UUID {}", debugUuid);
            return new ResponseEntity<>(project, HttpStatus.OK);

        }catch (ProjectNotFoundException p){
            log.error("UUID {}, ProjectNotFoundException in Get User BY Id API, Exception {}", debugUuid, p.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, p.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            log.error("UUID {}, Getting Exception in fetch Project BY Id API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For Deleting Project By I'd
     *
     * @return ResponseEntity<Project>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{projectId}")
    public ResponseEntity<?> deletedById(@Valid @PathVariable Long projectId) {
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Inside Delete Project By Id,UUID {} ", projectId);
            boolean project = projectService.deletedByProjectId(projectId);
            log.info("Delete Project By Id API called ,UUID {} ", projectId);
            return new ResponseEntity<>(new CustomResponse<>(true, "Project Deleted Successfully", project), HttpStatus.OK);
        }catch (ProjectNotFoundException p){
            log.error("UUID {}, ProjectNotFoundException in Delete Project BY Id API, Exception {}", debugUuid, p.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, p.getMessage(), false), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            log.error("UUID {}, Getting Exception in Delete Project BY Id API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), false),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For Updating Project details By I'd
     *
     * @return ResponseEntity<Project>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{projectId}")
    public ResponseEntity<?> updateById(@Valid @PathVariable Long projectId, @Valid @RequestBody Project project) {
        String debugUuid = UUID.randomUUID().toString();
        try{
            log.info("Inside Updating project with id in service with UUID{}, ", debugUuid);
            Optional<Project> project1 = Optional.ofNullable(projectService.updateProjectById(projectId, project));
            return project1.map(value -> new ResponseEntity<>(new CustomResponse<>(true, "Update Project Details Successfully", value), HttpStatus.OK)).orElse(null);

        }catch (DuplicateProjectName dp){
            log.error("UUID {} Getting exception while Updating a new project Exception {}", debugUuid, dp.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, "Project name is already in use", null),HttpStatus.CONFLICT);
        }
        catch (ProjectNotFoundException p){
            log.error("UUID {}, ProjectNotFoundException in Delete Project BY Id API, Exception {}", debugUuid, p.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, p.getMessage(), false), HttpStatus.NOT_FOUND);
        }
        catch (DataNotPresent dnp){
            log.error("UUID {} Getting exception while adding a new project Exception {}", debugUuid, dnp.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, "Do not Pass Empty String, Please Fill the data Properly", null),HttpStatus.NO_CONTENT);
        }
        catch (Exception e) {
            log.error("UUID {}, Getting Exception in Update Project BY Id API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), false),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For Assigning Team To A Project
     *
     * @return ResponseEntity<?>
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{projectId}/teams")
    public ResponseEntity<?> assignTeamToProject(@PathVariable Long projectId, @RequestBody List<Long> teamIds) {
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Assign Team To Project API Called, UUID{}", debugUuid);
            List<TeamDTO> addedTeams = projectService.assignTeams(projectId, teamIds);
            if (addedTeams != null) {
                log.info("Assign Team To Project API Returning Response Entity With OK, UUID{}", debugUuid);
                return new ResponseEntity<>(new CustomResponse<>(true, "Teams Successfully Added", addedTeams), HttpStatus.OK);
            } else {
                log.info("UUID {} Project Not Created", debugUuid);
                return new ResponseEntity<>(new CustomResponseMessage(false, "Failed. Please Try Again"), HttpStatus.BAD_GATEWAY);
            }
        }catch (ProjectNotFoundException e){
            log.error("UUID {} Getting ProjectNotFoundException In Assign Team To Project, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponseMessage(false, e.getMessage()),HttpStatus.NOT_FOUND);
        }
        catch (TeamNotFoundException e){
            log.error("UUID {} Getting TeamNotFoundException in Assign Team To Project API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponseMessage(false, e.getMessage()),HttpStatus.NOT_FOUND);
        }
        catch (TeamAlreadyPresentInTheProjectException e){
            log.error("UUID {} Getting TeamAlreadyPresentInTheProjectException in Assign Team To Project API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponseMessage(false, e.getMessage()),HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            log.error("UUID {} Getting Exception in Assign Team To Project, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * For Delete Team From Project
     *
     * @return ResponseEntity<?>
     */
    @DeleteMapping("/{projectId}/teams/{teamId}")
    public ResponseEntity<?> deletedTeamFromProject(@PathVariable("projectId") Long projectId, @PathVariable("teamId") Long teamId) {
        String debugUuid = UUID.randomUUID().toString();
        try {
            log.info("Inside Delete Team From Project,UUID {} ", projectId);
            boolean isDeleted = projectService.deletedTeamFromProject(projectId, teamId);

            return new ResponseEntity<>(new CustomResponseMessage(true, "Team Removed Successfully"), HttpStatus.OK);
        }catch (ProjectNotFoundException p){
            log.error("UUID {}, ProjectNotFoundException in Delete Project BY Id API, Exception {}", debugUuid, p.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, p.getMessage(), false), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            log.error("UUID {}, Getting Exception in Delete Project BY Id API, Exception {}", debugUuid, e.getMessage());
            return new ResponseEntity<>(new CustomResponse<>(false, e.getMessage(), false),HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
