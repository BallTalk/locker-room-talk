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
        List<Menu> menus = menuRepository.findAllByVisibleYn("Y");
        if (menus.isEmpty()) throw MenuException.menuNotFound();

        return menus;
    }

}
