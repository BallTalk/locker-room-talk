package com.locker.user.domain;

import java.util.Optional;

public interface UserRepository {

    void deleteAll();

    void save(User user);

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByProviderAndProviderId(Provider provider, String oauthId);

    Optional<User> findByPhoneNumber(String normalizedPhoneNumber);

    Optional<User> findById(Long userId);
}
