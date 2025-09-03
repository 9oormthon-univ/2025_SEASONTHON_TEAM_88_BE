package com.eventory.server.domain.member.controller;

import com.eventory.server.domain.member.dto.MemberRequestDTO;
import com.eventory.server.domain.member.dto.MemberResponseDTO;
import com.eventory.server.domain.member.service.MemberCommandService;
import com.eventory.server.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "일반 로그인 API",
            description = "아이디와 비밀번호를 정확히 입력하면 인증에 성공하며, Access Token이 발급됩니다.")
    @PostMapping("/auth/login")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> localLogin (@RequestBody MemberRequestDTO.LoginDTO request){
        return ApiResponse.onSuccess(memberCommandService.loginMember(request));
    }

    @Operation(summary = "카카오 로그인 API", description = "카카오 로그인 및 회원 가입을 진행하는 API입니다. 인가코드를 넘겨주세요.")
    @GetMapping("/auth/kakao")
    public ApiResponse<MemberResponseDTO.LoginResultDTO> kakaoLogin(@RequestParam("code") String code) {
        return ApiResponse.onSuccess(memberCommandService.kakaoLogin(code));
    }
}
