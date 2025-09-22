package com.locker.menu.infra;

import com.locker.menu.domain.Menu;
import com.locker.menu.domain.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {

    private final MenuJpaRepository menuJpaRepository;

    @Override
    public List<Menu> findAllByVisibleYn(String y) {
        return menuJpaRepository.findAllByVisibleYn(y);
    }
}
