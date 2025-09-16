package com.locker.team.domain;

import com.locker.common.exception.base.CustomException;
import com.locker.common.exception.model.ErrorCode;

public class TeamException extends CustomException {
    private TeamException(ErrorCode code) {
        super(code);
    }

    public static TeamException teamNotFound() {
        return new TeamException(ErrorCode.TEAM_NOT_FOUND);
    }

}