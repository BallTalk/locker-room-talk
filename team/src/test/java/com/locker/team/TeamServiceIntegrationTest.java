package com.locker.team;

import com.locker.team.domain.Team;
import com.locker.team.domain.TeamRepository;
import com.locker.team.domain.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class TeamServiceIntegrationTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository; // ✅ 추가

    @Test
    void 팀_코드로_조회시_DB에_존재하는팀이면_Team을반환한다() {
        // given
        String teamCode = "TEAM001";
        Team team = Team.builder()
                .code(teamCode)
                .nameKr("두산 베어스")
                .nameEn("DOOSAN_BEARS")
                .isActive("Y")
                .logoUrl("http://example.com/logo.png")
                .createdBy("tester")
                .createdAt(LocalDateTime.now())
                .updatedBy("tester")
                .updatedAt(LocalDateTime.now())
                .build();
        teamRepository.save(team); // ✅ repository 직접 저장

        // when
        Team result = teamService.findByTeamCode(teamCode);

        // then
        assertNotNull(result);
        assertEquals(teamCode, result.getCode());
        assertEquals("두산 베어스", result.getNameKr());
        assertEquals("DOOSAN_BEARS", result.getNameEn());
    }
}
