package com.eventory.server.domain.party.controller;

import com.eventory.server.domain.party.dto.PartyRequestDTO;
import com.eventory.server.domain.party.dto.PartyResponseDTO;
import com.eventory.server.domain.party.service.PartyCommandService;
import com.eventory.server.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parties")
@RequiredArgsConstructor
public class PartySurveyController {

    private final PartyCommandService partyCommandService;

    @Operation(summary = "설문 기반 맞춤 패키지 추천 API - 특별히 준비 페이지 다음 버튼",
            description = "설문 결과에 따라 가격대 별 추천 패키지를 반환합니다.")
    @PostMapping("/survey")
    public ApiResponse<PartyResponseDTO.CustomPackageResponse> getCustomPackages(
            @RequestBody PartyRequestDTO.PartySurveyRequest request){
        return ApiResponse.onSuccess(partyCommandService.createParty(request));
    }
}