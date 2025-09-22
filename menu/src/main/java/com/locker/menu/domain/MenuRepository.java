package com.locker.menu.domain;

import java.util.List;
import java.util.Optional;

public interface MenuRepository {
    List<Menu> findAllByVisibleYn(String y);
}
