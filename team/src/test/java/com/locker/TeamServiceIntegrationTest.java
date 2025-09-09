package com.locker;

import com.locker.domain.Team;
import com.locker.domain.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TeamServiceIntegrationTest {

    private final TeamService teamService;

    @Autowired
    TeamServiceIntegrationTest(TeamService teamService) {
        this.teamService = teamService;
    }

    @Test
    void 팀_코드로_조회시_DB에_존재하는팀이면_Team을반환한다() {
        // given
        String teamCode = "TEAM001";

        // when
        Team result = teamService.findByTeamCode(teamCode);

        // then
        assertNotNull(result);
        assertEquals(teamCode, result.getCode());
        assertEquals("두산 베어스", result.getNameKr());
        assertEquals("DOOSAN_BEARS", result.getNameEn());
    }
}
