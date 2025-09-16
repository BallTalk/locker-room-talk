package com.locker.team.domain;

import java.util.Optional;

public interface TeamRepository {
    Optional<Team> findByCode(String teamCode);
}
