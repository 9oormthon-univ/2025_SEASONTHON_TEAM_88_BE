package com.eventory.server.domain.party.controller;

import com.eventory.server.domain.party.dto.PartyResponseDTO;
import com.eventory.server.domain.party.dto.request.CreatePartyRequest;
import com.eventory.server.domain.party.dto.request.SelectPartyRequest;
import com.eventory.server.domain.party.dto.response.CreatePartyResponse;
import com.eventory.server.domain.party.dto.response.DeletePartyResponse;
import com.eventory.server.domain.party.dto.response.SelectPartyResponse;
import com.eventory.server.domain.party.dto.response.SelectPartyListResponse;
import com.eventory.server.domain.party.service.PartyQueryService;
import com.eventory.server.domain.party.dto.response.main.MyPartyResponse;
import com.eventory.server.domain.party.service.PartyService;
import com.eventory.server.global.apipayload.ApiResponse;
import com.eventory.server.global.apipayload.code.status.SuccessStatus;
import com.eventory.server.global.security.handler.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parties")
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;
    private final PartyQueryService partyQueryService;

    @Operation(summary = "내 파티 리스트 API")
    @GetMapping("/my")
    public ApiResponse<MyPartyResponse> myPartyList(
            @AuthUser Long memberId
    ) {
        MyPartyResponse myPartyResponse = partyService.partyMain(memberId);
        return ApiResponse.of(SuccessStatus.PARTY_LIST_OK, myPartyResponse);
    }

    @Operation(summary = "내 파티 삭제 API")
    @DeleteMapping("/{partyId}")
    public ApiResponse<DeletePartyResponse> deleteParty(
            @AuthUser Long memberId,
            @PathVariable(name = "partyId") Long partyId
    ) {
        DeletePartyResponse deletePartyResponse = partyService.deleteParty(memberId, partyId);
        return ApiResponse.of(SuccessStatus.PARTY_DELETE_OK, deletePartyResponse);
    }

    @Operation(summary = "내가 찜한 상품과 비슷한 상품 조회 API")
    @GetMapping("/similar")
    public ApiResponse<List<PartyResponseDTO.ProductInfo>> getSimilarProductsToLiked(
            @AuthUser Long userId
    ) {
        return ApiResponse.onSuccess(partyQueryService.getSimilarItem(userId));
    }

    @Operation(summary = "내 파티 생성 API")
    @PostMapping("/parties/new")
    public ApiResponse<CreatePartyResponse> createParty(
            @AuthUser Long memberId,
            @RequestBody CreatePartyRequest createPartyRequest
    ) {
        CreatePartyResponse createPartyResponse = partyService.createParty(memberId, createPartyRequest);
        return ApiResponse.of(SuccessStatus.PARTY_CREATE_OK, createPartyResponse);
    }

    @PostMapping("/select/{partyId}")
    public ApiResponse<SelectPartyResponse> selectParty(
            @AuthUser Long memberId,
            @PathVariable(name = "partyId") Long partyId,
            @RequestBody List<SelectPartyRequest> selectPartyRequests
    ) {
        SelectPartyResponse selectPartyResponse = partyService.selectParty(memberId, partyId, selectPartyRequests);
        return ApiResponse.of(SuccessStatus.TODO_CREATE_OK, selectPartyResponse);

    @GetMapping("/select")
    public ApiResponse<List<SelectPartyListResponse>> selectPartyList(
            @AuthUser Long memberId
    ) {
        List<SelectPartyListResponse> selectPartyListResponses = partyService.selectPartyList(memberId);
        return ApiResponse.of(SuccessStatus.PARTY_LIST_OK, selectPartyListResponses);
    }
}
