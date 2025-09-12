package com.locker.team.infra;

import com.locker.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TeamJpaRepository extends JpaRepository<Team, String> {

    Optional<Team> findByCode(String teamCode);
}
