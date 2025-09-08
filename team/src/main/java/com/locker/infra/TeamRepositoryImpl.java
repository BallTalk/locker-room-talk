package com.locker.infra;

import com.locker.domain.Team;
import com.locker.domain.TeamRepository;
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
