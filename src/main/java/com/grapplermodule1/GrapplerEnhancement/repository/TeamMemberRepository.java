package com.grapplermodule1.GrapplerEnhancement.repository;

import com.grapplermodule1.GrapplerEnhancement.dtos.TeamMembersDTO;
import com.grapplermodule1.GrapplerEnhancement.entities.TeamMembers;
import com.grapplermodule1.GrapplerEnhancement.entities.Users;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMembers, Long> {

    @Query("SELECT NEW com.grapplermodule1.GrapplerEnhancement.dtos.TeamMembersDTO(u.id, u.name, u.email, u.designation) " +
            "FROM Users u " +
            "JOIN u.teamMembers tm " +
            "WHERE tm.team.id = :teamId")
    Optional<List<TeamMembersDTO>> findUsersByTeamId(Long teamId);

    @Query("SELECT NEW com.grapplermodule1.GrapplerEnhancement.dtos.TeamMembersDTO(u.id, u.name, u.email, u.designation) " +
            "FROM Users u " +
            "JOIN u.teamMembers tm " +
            "WHERE tm.id = :memberId")
    Optional<TeamMembersDTO> findUsersByTeamMemberId(Long memberId);

    Optional<TeamMembers> findByTeamIdAndUserId(Long teamId, Long userId);

    @Transactional
    @Modifying
    void deleteByTeamIdAndUserId(Long teamId, Long userId);

}
