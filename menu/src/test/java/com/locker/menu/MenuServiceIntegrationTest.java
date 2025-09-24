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
    void 앱_가동시_캐시된_메뉴가_초기화된다() {
        // given : @SpringBootTest -> 스프링 컨텍스트 가동 시 @PostConstruct 실행

        // when
        List<Menu> cached = menuService.getAllMenus();

        // then
        assertFalse(cached.isEmpty()); // 캐시에 이미 값이 올라가 있음
    }

}
