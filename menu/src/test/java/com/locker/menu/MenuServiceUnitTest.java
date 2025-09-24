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
    void 메뉴조회시_visibleYn이_Y인_메뉴가_DB에_없으면_MENU_NOT_FOUND_예외가_발생한다() {
        // given
        when(menuRepository.findAllByVisibleYn("Y")).thenReturn(List.of());

        // when & then
        MenuException ex = assertThrows(MenuException.class,
                () -> menuService.refresh()); // init 으로 바껴서 getAllMenus() 대신 refresh() 호출

        assertEquals(ErrorCode.MENU_NOT_FOUND, ex.getErrorCode());
        verify(menuRepository).findAllByVisibleYn("Y");
    }


}
