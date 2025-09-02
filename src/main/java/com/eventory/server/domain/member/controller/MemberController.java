package com.eventory.server.domain.member.controller;

import com.eventory.server.domain.member.dto.MemberRequestDTO;
import com.eventory.server.domain.member.service.MemberCommandService;
import com.eventory.server.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberCommandService memberCommandService;

    @Operation(summary = "일반 회원가입 API",
            description = "아이디는 6~10자이며, 비밀번호는 8~15자이고 영문, 숫자, 특수문자를 반드시 포함해야 합니다." +
                    "비밀번호 확인란(password2)은 password1과 반드시 일치해야 합니다.")
    @PostMapping("/auth/signup")
    public ApiResponse<String> joinService (@Valid @RequestBody MemberRequestDTO.joinDTO request){
        memberCommandService.joinMember(request);
        return ApiResponse.onSuccess("회원가입이 완료되었습니다.");
    }
}
