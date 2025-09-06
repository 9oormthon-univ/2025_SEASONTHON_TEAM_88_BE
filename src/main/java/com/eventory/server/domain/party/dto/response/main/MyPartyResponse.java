package com.eventory.server.domain.party.dto.response.main;

import java.util.List;

public record MyPartyResponse(
        Long partyId,
        String partyName,
        Double progressRate,
        List<TodoResponse> todoResponses
) {
}
