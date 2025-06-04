package com.locker.user.infra;

import com.locker.user.domain.Provider;
import com.locker.user.domain.Status;
import com.locker.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByLoginId(String loginId);

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByProviderAndProviderId(Provider provider, String oauthId);

    Optional<User> findByLoginIdAndStatusIn(String loginId, List<Status> statuses);
}
