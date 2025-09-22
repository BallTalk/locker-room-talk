package com.locker.menu.infra;

import com.locker.menu.domain.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MenuJpaRepository extends JpaRepository<Menu, Long> {
}
