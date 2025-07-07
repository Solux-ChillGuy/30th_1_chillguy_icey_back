package com.project.icey.app.dto;

import com.project.icey.app.domain.BalanceGame;

public record BalanceGameDto(
        Long id,
        String option1,
        String option2,
        Long teamId
) {
    public static BalanceGameDto from(BalanceGame game) {
        return new BalanceGameDto(
                game.getId(),
                game.getOption1(),
                game.getOption2(),
                game.getTeam().getTeamId()
        );
    }
}
