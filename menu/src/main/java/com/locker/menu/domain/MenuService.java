package com.locker.menu.domain;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private List<Menu> cachedMenus;

    // 앱 시작 직후(@PostConstruct) 실행
    // DB 연결이 아직 준비되지 않을 수 있는 환경(컨테이너 오케스트레이션 등)이라면
    // @EventListener(ApplicationReadyEvent.class) 로 변경하기
    @PostConstruct
    public void init() {
        this.cachedMenus = List.copyOf(menuRepository.findAllByVisibleYn("Y"));
        if (cachedMenus.isEmpty()) throw MenuException.menuNotFound();
    }

    public List<Menu> getAllMenus() {
        return cachedMenus;
    }

    public void refresh() {
        this.cachedMenus = List.copyOf(menuRepository.findAllByVisibleYn("Y"));
        if (cachedMenus.isEmpty()) throw MenuException.menuNotFound();
    }

}
