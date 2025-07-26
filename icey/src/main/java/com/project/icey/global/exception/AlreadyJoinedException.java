package com.project.icey.global.exception;

import com.project.icey.global.exception.model.CoreApiException;
import lombok.Getter;

@Getter
public class AlreadyJoinedException extends CoreApiException {
    private final Long teamId;

    public AlreadyJoinedException(Long teamId) {
        super(ErrorCode.ALREADY_JOINED);
        this.teamId = teamId;
    }
}
