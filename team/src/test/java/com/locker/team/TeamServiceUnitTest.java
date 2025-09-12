package com.locker.team;

import com.locker.team.domain.Team;
import com.locker.team.domain.TeamException;
import com.locker.team.domain.TeamRepository;
import com.locker.team.domain.TeamService;
import com.locker.exception.model.ErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceUnitTest {

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    void 팀_코드로_조회시_존재하는팀이면_Team을반환한다() {
        // given
        Team team = Team.builder()
                .code("TEAM001")
                .nameKr("두산 베어스")
                .nameEn("Doosan Bears")
                .build();

        when(teamRepository.findByCode("TEAM001"))
                .thenReturn(Optional.of(team));

        // when
        Team result = teamService.findByTeamCode("TEAM001");

        // then
        assertSame(team, result);
        verify(teamRepository).findByCode("TEAM001");
    }

    @Test
    void 팀_코드로_조회시_존재하지않는팀이면_TEAM_NOT_FOUND_예외가_발생한다() {
        // given
        when(teamRepository.findByCode("NO_TEAM_1515"))
                .thenReturn(Optional.empty());

        // when & then
        TeamException ex = assertThrows(TeamException.class,
                () -> teamService.findByTeamCode("NO_TEAM_1515"));
        assertEquals(ErrorCode.TEAM_NOT_FOUND, ex.getErrorCode());
        verify(teamRepository).findByCode("NO_TEAM_1515");
    }
}
