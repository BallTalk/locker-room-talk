package com.locker.team.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;

    public Team findByTeamCode(String teamCode) {
        return teamRepository.findByCode(teamCode)
                .orElseThrow(TeamException::teamNotFound);
    }

}
