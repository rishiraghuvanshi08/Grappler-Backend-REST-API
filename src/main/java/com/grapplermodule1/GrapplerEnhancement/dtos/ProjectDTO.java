package com.grapplermodule1.GrapplerEnhancement.dtos;

import java.util.List;

public class ProjectDTO {
    private Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<TeamDTO> getTeams() {
        return teams;
    }

    public void setTeams(List<TeamDTO> teams) {
        this.teams = teams;
    }

    public ProjectDTO(Long id, String name, List<TeamDTO> teams) {
        this.id = id;
        this.name = name;
        this.teams = teams;
    }
    private List<TeamDTO> teams;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
