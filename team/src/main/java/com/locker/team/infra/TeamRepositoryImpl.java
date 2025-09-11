package com.locker.team.infra;

import com.locker.team.domain.Team;
import com.locker.team.domain.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TeamRepositoryImpl implements TeamRepository {

    private final TeamJpaRepository teamJpaRepository;


    @Override
    public Optional<Team> findByCode(String teamCode) {
        return teamJpaRepository.findByCode(teamCode);
    }
}
