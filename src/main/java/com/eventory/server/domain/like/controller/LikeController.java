package com.eventory.server.domain.like.controller;

import com.eventory.server.domain.like.dto.response.RegisterLikeResponse;
import com.eventory.server.domain.like.dto.request.RegisterLikeRequest;
import com.eventory.server.domain.like.service.LikeService;
import com.eventory.server.global.apipayload.ApiResponse;
import com.eventory.server.global.apipayload.code.status.SuccessStatus;
import com.eventory.server.global.security.handler.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/likes")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    @Operation(summary = "찜 등록 API")
    @PostMapping
    public ApiResponse<RegisterLikeResponse> registerLike(
            @AuthUser Long memberId,
            @RequestBody RegisterLikeRequest registerLikeRequest
    ) {
        RegisterLikeResponse registerLikeResponse = likeService.registerLike(memberId, registerLikeRequest);
        return ApiResponse.of(SuccessStatus.LIKE_REGISTER_OK, registerLikeResponse);
    }
}
