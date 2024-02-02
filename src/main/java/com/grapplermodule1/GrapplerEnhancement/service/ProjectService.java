package com.grapplermodule1.GrapplerEnhancement.service;

import com.grapplermodule1.GrapplerEnhancement.customexception.*;
import com.grapplermodule1.GrapplerEnhancement.dtos.ProjectDTO;
import com.grapplermodule1.GrapplerEnhancement.dtos.TeamDTO;
import com.grapplermodule1.GrapplerEnhancement.entities.Project;
import com.grapplermodule1.GrapplerEnhancement.entities.Team;
import com.grapplermodule1.GrapplerEnhancement.repository.ProjectRepository;
import com.grapplermodule1.GrapplerEnhancement.repository.TeamRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProjectService {
  @Autowired
  private ProjectRepository projectRepository;

  @Autowired
  private TeamService teamService;

  @Autowired
  private TeamRepository teamRepository;
  private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

  /**
   * Getting the list of users
   *
   * @return List
   **/
  public List<ProjectDTO> getAllProjects() {
    String debugUuid = UUID.randomUUID().toString();
    try {
      log.info("Getting List of all Projects");
      List<ProjectDTO> projectDTOList = projectRepository.findListOfProjects();

      if (!projectDTOList.isEmpty()) {
        log.info("Got List of all Projects that is present in db");
        projectDTOList.forEach(projectDTO -> {
          projectDTO.setTeams(getTeamList(projectDTO.getId()));
        });
        return projectDTOList;
      } else {
        log.info("Throws exception because there no project found with UUID {}", debugUuid);
        throw new ProjectNotFoundException("Project not Found");
      }
    } catch (Exception e) {
      log.info("Exception In Fetch All Projects Exception {}", e.getMessage());
      throw e;
    }
  }

  /**
   * For Getting List Of Teams
   *
   * @return List<TeamDTO>
   */
  public List<TeamDTO> getTeamList(Long projectId) {
    try {
      log.info("Get Team By Id Called in Hierarchy Service");
      List<TeamDTO> listOfTeams = teamRepository.searchTeamById(projectId);
      listOfTeams.forEach(ls -> {
        ls.setTeamMembers(teamService.getMembersList(ls.getId()));
      });

      if (!listOfTeams.isEmpty()) {
        log.info("Get Team Member By Id returning List of TeamMemberDTO");
      }
      return listOfTeams;
    } catch (Exception e) {
      log.error("Exception In Get Members List Service in Hierarchy Service Exception {}", e.getMessage());
      throw e;
    }
  }

  /**
   * For Adding A New Project
   *
   * @return Project
   */
  public Project createProject(Project project) {
    String debugUuid = UUID.randomUUID().toString();
    try {
      if (projectRepository.existsByName(project.getName())) {
        log.error("Duplicate Project name in, UUID {}", debugUuid);
        throw new DuplicateProjectName("Project Name is already in use");
      }
      if (Objects.equals(project.getName(), "")) {
        log.info("new Project detail is Empty, UUID {}", debugUuid);
        throw new DataNotPresent("Do not Pass Empty String");
      }
      log.info("Create new Project API Called, UUID {}", debugUuid);
      return projectRepository.save(project);
    } catch (Exception e) {
      log.error("Exception In Add Project Service Exception {}", e.getMessage());
      throw e;
    }
  }

  /**
   * For getting A Project with id
   *
   * @return Project
   */
  public ProjectDTO getProjectById(Long projectId) {
    String debugUuid = UUID.randomUUID().toString();
    try {
      log.info("Fetching Project with id : " + projectId);
      Optional<ProjectDTO> project = projectRepository.findProjectById(projectId);
      if (project.isPresent()) {
        log.info("Project found with id : " + projectId);
        project.get().setTeams(getTeamList(project.get().getId()));
        return project.get();
      } else {
        log.error("Throws exception because there no project found with UUID {}", debugUuid);
        throw new ProjectNotFoundException("Project not Found With Id: " + projectId);
      }
    } catch (Exception e) {
      log.error("Exception In Fetching Project with id, Exception {}", e.getMessage());
      throw e;
    }
  }

  /**
   * For deleting A Project with id
   *
   * @return boolean
   */
  public boolean deletedByProjectId(Long projectId) {
    String debugUuid = UUID.randomUUID().toString();
    try {
      log.info("Deleting project with id in service is called, project Id {}", projectId);
      Optional<Project> project = projectRepository.findById(projectId);
      if (project.isPresent()) {
        log.info("Deleting project with id successfully in service with UUID {}, ", debugUuid);
        projectRepository.deleteById(projectId);
        return true;
      } else {
        log.error("Throws exception because there no project found with UUID {}", debugUuid);
        throw new ProjectNotFoundException("Project not Found");
      }
    } catch (Exception e) {
      log.error("Exception In Deleting Project with id, Exception {}", e.getMessage());
      throw e;
    }

  }

  /**
   * For updating A Project with id
   *
   * @return Project
   */
  public Project updateProjectById(Long id, Project project) {
    String debugUuid = UUID.randomUUID().toString();
    try {
      log.info("Inside Updating project with id in service with uuid{}, ", debugUuid);
      Project update = projectRepository.findById(id).orElseThrow(() -> new ProjectNotFoundException("Project not found"));
      update.setName(project.getName());
      if (projectRepository.existsByName(project.getName())) {
        log.error("Duplicate Project name in, UUID {}", debugUuid);
        throw new DuplicateProjectName("Project Name is already in use");
      }
      if (Objects.equals(project.getName(), "")) {
        log.info("Update Project detail is Empty, UUID {}", debugUuid);
        throw new DataNotPresent("Do not Pass Empty String");
      }
      update.setTeams(project.getTeams());
      log.info("Updating project with id successfully in service with uuid{}, ", debugUuid);
      return projectRepository.save(update);
    } catch (Exception e) {
      log.error("Exception In Deleting Project with id, Exception {}", e.getMessage());
      throw e;
    }
  }

  /**
   * For Assigning Team
   *
   * @return Boolean
   */
  public List<TeamDTO> assignTeams(Long projectId, List<Long> teamIds) {
    String debugUuid = UUID.randomUUID().toString();
    try {
      log.info("Inside Assign Team in Project service with UUID{}, ", debugUuid);
      Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project not found With ID : " + projectId));

      List<TeamDTO> addedTeams = new ArrayList<>();

      Set<Team> projectTeams = project.getTeams();

      for (Long teamId : teamIds) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException("Team Not Found With ID : " + teamId));

        if (projectTeams.contains(team)) {
          throw new TeamAlreadyPresentInTheProjectException("Team Is Already Present In The Project");
        }

        projectTeams.add(team);

        addedTeams.add(teamRepository.findTeamById(teamId).orElseThrow(() -> new ResourseNotFoundException(("Team Not Found With ID : " + teamId))));
      }

      project.setTeams(projectTeams);
      projectRepository.save(project);

      return addedTeams;
    } catch (Exception e) {
      log.error("Exception In Assign Team Service, Exception {}", e.getMessage());
      throw e;
    }
  }

  public Boolean deletedTeamFromProject(Long projectId, Long teamId) {
    try {
      log.info("Deleting project with id in service is called, project Id {}", projectId);

      Project project = projectRepository.findById(projectId).orElseThrow(() -> new ProjectNotFoundException("Project not found With ID : " + projectId));

      Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException("Team Not Found With ID : " + teamId));

      Set<Team> projectTeams = project.getTeams();

      projectTeams = projectTeams.stream().filter((t) -> t.getId() != teamId).collect(Collectors.toSet());

      project.setTeams(projectTeams);
      projectRepository.save(project);

      return true;
    } catch (Exception e) {
      log.error("Exception In Deleting Project with id, Exception {}", e.getMessage());
      throw e;
    }
  }

}
