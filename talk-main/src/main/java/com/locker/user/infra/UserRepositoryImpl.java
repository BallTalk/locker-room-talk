package com.locker.user.infra;

import com.locker.user.domain.Provider;
import com.locker.user.domain.Status;
import com.locker.user.domain.User;
import com.locker.user.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    @Override
    public void deleteAll() {
        userJpaRepository.deleteAll();
    }

    @Override
    public void save(User user) {
        userJpaRepository.save(user);
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return userJpaRepository.existsByLoginId(loginId);
    }

    @Override
    public Optional<User> findByLoginId(String loginId) {
        return userJpaRepository.findByLoginId(loginId);
    }

    @Override
    public Optional<User> findByProviderAndProviderId(Provider provider, String oauthId) {
        return userJpaRepository.findByProviderAndProviderId(provider, oauthId);
    }




}
