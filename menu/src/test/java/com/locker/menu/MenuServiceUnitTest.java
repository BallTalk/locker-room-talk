package com.locker.menu;

import com.locker.common.exception.model.ErrorCode;
import com.locker.menu.domain.Menu;
import com.locker.menu.domain.MenuException;
import com.locker.menu.domain.MenuRepository;
import com.locker.menu.domain.MenuService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServiceUnitTest {

    @Mock
    private MenuRepository menuRepository;

    @InjectMocks
    private MenuService menuService;


    @Test
    void getAllMenus_을_호출후에_refresh_하면_repository를_재호출하지않고_캐시된_값을_반환한다() {
        // given
        List<Menu> mockMenus = List.of(
                Menu.builder().id(1L).name("홈")
                        .position("TOP").type("LINK").path("/")
                        .sortOrder(1).visibleYn("Y")
                        .createdBy("tester").updatedBy("tester")
                        .build()
        );
        when(menuRepository.findAllByVisibleYn("Y")).thenReturn(mockMenus);

        // when
        menuService.refresh();
        menuService.getAllMenus();
        menuService.getAllMenus();

        // then
        verify(menuRepository, times(1)).findAllByVisibleYn("Y"); // 한 번만 호출됨
    }

}
