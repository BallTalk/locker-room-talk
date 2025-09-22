package com.locker.home.application;

import com.locker.home.api.HomeResponse;
import com.locker.menu.domain.Menu;
import com.locker.menu.domain.MenuService;
import com.locker.team.domain.TeamService;
import com.locker.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeFacade {

    private final MenuService menuService;
    private final TeamService teamService;
    private final UserService userService;

    @Transactional
    public HomeResponse getHome(String loginId) {
        List<Menu> menus = menuService.getAllMenus();

        return new HomeResponse();
    }
}
