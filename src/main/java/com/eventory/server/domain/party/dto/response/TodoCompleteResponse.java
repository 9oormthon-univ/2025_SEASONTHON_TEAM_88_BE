package com.eventory.server.domain.party.dto.response;

public record TodoCompleteResponse(
        Long todoId,
        Boolean isCompleted
) {
}
