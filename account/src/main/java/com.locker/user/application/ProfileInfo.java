package com.locker.user.application;

import com.locker.team.domain.Team;
import com.locker.user.domain.User;

public record ProfileInfo(
        Long    id,
        String  loginId,
        String  provider,
        String  nickname,
        String  teamCode,
        String  teamNameKr,
        String  teamNameEn,
        String  teamLogoUrl,
        String  profileImageUrl,
        String  statusMessage,
        String  status
) {
    public static ProfileInfo from(User user, Team team) {
        return new ProfileInfo(
                user.getId(),
                user.getLoginId(),
                user.getProvider() != null ? user.getProvider().name() : "LOCAL",
                user.getNickname(),
                user.getTeamCode(),
                team.getNameKr(),
                team.getNameEn(),
                team.getLogoUrl(),
                user.getProfileImageUrl(),
                user.getStatusMessage(),
                user.getStatus() != null ? user.getStatus().name() : "UNKNOWN"
        );
    }
}