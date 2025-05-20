package com.locker.user.domain;

import java.util.Optional;

public interface UserRepository {

    void save(User user);

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);

}
