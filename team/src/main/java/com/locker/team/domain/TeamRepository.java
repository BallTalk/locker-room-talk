package com.locker.team.domain;

import java.util.List;
import java.util.Optional;

public interface TeamRepository {

    Team save(Team team);

    Optional<Team> findByCode(String teamCode);

}
