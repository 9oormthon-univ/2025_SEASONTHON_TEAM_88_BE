package com.eventory.server.domain.party.controller;

import com.eventory.server.domain.party.dto.response.DeletePartyResponse;
import com.eventory.server.domain.party.dto.response.main.MyPartyResponse;
import com.eventory.server.domain.party.service.PartyService;
import com.eventory.server.global.apipayload.ApiResponse;
import com.eventory.server.global.apipayload.code.status.SuccessStatus;
import com.eventory.server.global.security.handler.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parties")
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;

    @GetMapping("/my")
    public ApiResponse<MyPartyResponse> myPartyList(
            @AuthUser Long memberId
    ) {
        MyPartyResponse myPartyResponse = partyService.partyMain(memberId);
        return ApiResponse.of(SuccessStatus.PARTY_LIST_OK, myPartyResponse);
    }

    @Operation(summary = "파티 삭제 API")
    @DeleteMapping("/{partyId}")
    public ApiResponse<DeletePartyResponse> deleteParty(
            @AuthUser Long userId,
            @PathVariable(name = "partyId") Long partyId
    ) {
        DeletePartyResponse deletePartyResponse = partyService.deleteParty(userId, partyId);
        return ApiResponse.of(SuccessStatus.PARTY_DELETE_OK, deletePartyResponse);
    }
}
