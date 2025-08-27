package com.locker.infra;

import com.locker.domain.Provider;
import com.locker.domain.User;
import com.locker.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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

    @Override
    public Optional<User> findByPhoneNumber(String normalizedPhoneNumber) {
        return userJpaRepository.findByPhoneNumber(normalizedPhoneNumber);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

}
