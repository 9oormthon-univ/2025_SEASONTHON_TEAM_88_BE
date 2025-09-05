package com.eventory.server.domain.party.service;

import com.eventory.server.domain.party.dto.response.DeletePartyResponse;
import com.eventory.server.domain.party.entity.Party;
import com.eventory.server.domain.party.repository.PartyRepository;
import com.eventory.server.global.apipayload.code.status.ErrorStatus;
import com.eventory.server.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository partyRepository;

    /**
     * 내 파티 삭제
     * @param userId
     * @param partyId
     * @return DeletePartyResponse (partyId)
     */
    @Transactional
    public DeletePartyResponse deleteParty(Long userId, Long partyId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTY_NOT_FOUND));

        if (!party.getMember().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN_PARTY_DELETE);
        }

        partyRepository.delete(party);

        return new DeletePartyResponse(party.getId());
    }
}
