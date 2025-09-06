package com.eventory.server.domain.party.dto.response;

public record CreatePartyResponse(
        Long partyId,
        String partyName
) {
}
