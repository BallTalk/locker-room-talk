package com.locker.menu.domain;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {

    Menu save(Menu menu); // 추가

    List<Menu> findAllByVisibleYn(String y);

}
