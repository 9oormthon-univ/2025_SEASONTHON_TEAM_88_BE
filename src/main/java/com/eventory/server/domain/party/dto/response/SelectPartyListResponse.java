package com.eventory.server.domain.party.dto.response;

public record SelectPartyListResponse(
        Long partyId,
        String partyName
) {
}
