package com.locker.menu.domain;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public List<Menu> getAllMenus() {
        return Optional.of(menuRepository.findAll())
                .filter(menus -> !menus.isEmpty())
                .orElseThrow(MenuException::menuNotFound);
    }

}
