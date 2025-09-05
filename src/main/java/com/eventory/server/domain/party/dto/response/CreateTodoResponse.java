package com.eventory.server.domain.party.dto.response;

public record CreateTodoResponse(
        Long partyId,
        Long todoId,
        String task
) {
}
