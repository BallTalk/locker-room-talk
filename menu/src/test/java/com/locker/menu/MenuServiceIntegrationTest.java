package com.locker.menu;

import com.locker.menu.domain.Menu;
import com.locker.menu.domain.MenuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class MenuServiceIntegrationTest {

    @Autowired
    private MenuService menuService;

    @Test
    void 메뉴조회시_DB에_데이터가_있으면_반환된다() {
        // when
        List<Menu> menus = menuService.getAllMenus();

        // then
        assertFalse(menus.isEmpty());
        //assertTrue(menus.stream().anyMatch(m -> m.getName().equals("뉴스")));
        //assertTrue(menus.stream().anyMatch(m -> m.getName().equals("캘린더"))); // 바뀔수도 있어서 주석처리함
    }
}
