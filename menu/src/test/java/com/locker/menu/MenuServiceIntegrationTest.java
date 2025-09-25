package com.locker.menu;

import com.locker.menu.domain.Menu;
import com.locker.menu.domain.MenuRepository;
import com.locker.menu.domain.MenuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MenuServiceIntegrationTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Test
    void refresh_호출시_DB값이_캐싱되어_getAllMenus로_조회된다() { // data.sql 사용하지않아 init -> refresh 로 테스트 변경
        // given
        Menu menu = Menu.builder()
                .name("홈")
                .position("TOP")
                .type("LINK")
                .path("/")
                .sortOrder(1)
                .visibleYn("Y")
                .createdBy("tester")
                .updatedBy("tester")
                .build();
        menuRepository.save(menu);

        // when
        menuService.refresh();
        List<Menu> cached = menuService.getAllMenus();

        // then
        assertEquals(1, cached.size());
        assertEquals("홈", cached.get(0).getName());
    }
}