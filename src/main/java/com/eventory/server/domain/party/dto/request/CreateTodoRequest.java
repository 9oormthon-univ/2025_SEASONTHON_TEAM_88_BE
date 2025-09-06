package com.eventory.server.domain.party.dto.request;

public record CreateTodoRequest(
        Long partyId,
        String task
) {
}
