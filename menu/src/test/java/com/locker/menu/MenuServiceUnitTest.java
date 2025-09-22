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
    void 메뉴조회시_DB에_메뉴가_없으면_MENU_NOT_FOUND_예외가_발생한다() {
        // given
        when(menuRepository.findAll()).thenReturn(List.of());

        // when & then
        MenuException ex = assertThrows(MenuException.class,
                () -> menuService.getAllMenus());

        assertEquals(ErrorCode.MENU_NOT_FOUND, ex.getErrorCode());
        verify(menuRepository).findAll();
    }

    @Test
    void 메뉴조회시_DB에_메뉴가_존재하면_리스트가_그대로_반환된다() {
        // given
        Menu menu1 = mock(Menu.class);
        Menu menu2 = mock(Menu.class);
        when(menuRepository.findAll()).thenReturn(List.of(menu1, menu2));

        // when
        List<Menu> result = menuService.getAllMenus();

        // then
        assertEquals(2, result.size());
        assertSame(menu1, result.get(0));
        assertSame(menu2, result.get(1));
        verify(menuRepository).findAll();
    }

}
