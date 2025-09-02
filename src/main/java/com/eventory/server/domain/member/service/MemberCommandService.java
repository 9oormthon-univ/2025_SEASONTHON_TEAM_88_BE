package com.eventory.server.domain.member.service;

import com.eventory.server.domain.member.converter.MemberConverter;
import com.eventory.server.domain.member.dto.MemberRequestDTO;
import com.eventory.server.domain.member.dto.MemberResponseDTO;
import com.eventory.server.domain.member.entity.LoginInfo;
import com.eventory.server.domain.member.entity.Member;
import com.eventory.server.domain.member.repository.LoginInfoRepository;
import com.eventory.server.domain.member.repository.MemberRepository;
import com.eventory.server.global.apipayload.code.status.ErrorStatus;
import com.eventory.server.global.apipayload.exception.handler.MemberHandler;
import com.eventory.server.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MemberCommandService {

    private final LoginInfoRepository loginInfoRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.token.expiration.refresh}")
    private long refreshTokenExpiration;

    // 회원가입
    @Transactional
    public LoginInfo joinMember(MemberRequestDTO.joinDTO joinDTO) {

        validateUsername(joinDTO.getUsername());
        validateNickname(joinDTO.getNickname());
        validateEmail(joinDTO.getEmail());
        validatePhoneNumber(joinDTO.getPhoneNumber());

        if (!Objects.equals(joinDTO.getPassword1(), joinDTO.getPassword2())) {
            throw new MemberHandler(ErrorStatus.PASSWORD_MISMATCH);
        }

        validatePasswordComplexity(joinDTO.getPassword1());

        if (loginInfoRepository.existsByUsername(joinDTO.getUsername())) {
            throw new MemberHandler(ErrorStatus.DUPLICATE_USERNAME);
        }

        boolean exists = memberRepository.existsByNickname(joinDTO.getNickname());
        if (exists){
            throw new MemberHandler(ErrorStatus.DUPLICATE_NICKNAME);
        }

        Member joinMember = MemberConverter.toMember(joinDTO);
        memberRepository.save(joinMember);

        String encodedPassword = passwordEncoder.encode(joinDTO.getPassword1());
        LoginInfo joinLoginInfo = MemberConverter.toLocalLoginInfo(joinDTO, joinMember);
        joinLoginInfo.encodePassword(encodedPassword);

        return loginInfoRepository.save(joinLoginInfo);
    }

    // 일반 로그인
    public MemberResponseDTO.LoginResultDTO loginMember(MemberRequestDTO.LoginDTO loginDTO) {

        LoginInfo loginMemberLoginInfo = loginInfoRepository.findByUsernameWithUser(loginDTO.getUsername())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.LOGIN_INFO_NOT_FOUND));

        Member loginMember = loginMemberLoginInfo.getMember();

        if(!passwordEncoder.matches(loginDTO.getPassword(), loginMemberLoginInfo.getPassword())){
            throw new MemberHandler(ErrorStatus.PASSWORD_MISMATCH);
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                loginMemberLoginInfo.getUsername(),
                null,
                Collections.emptyList());

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        return MemberConverter.toLoginResultDTO(
                loginMember.getId(),
                accessToken,
                refreshToken
        );
    }

    private void validateUsername(String username) {
        if (username.length() < 6 || username.length() > 10) {
            throw new MemberHandler(ErrorStatus.INVALID_USERNAME_FORMAT);
        }
        if (!username.matches("^[a-zA-Z0-9]+$")) {
            throw new MemberHandler(ErrorStatus.INVALID_USERNAME_FORMAT);
        }
    }

    private void validateNickname(String nickname) {
        if (nickname.length() < 2 || nickname.length() > 12) {
            throw new MemberHandler(ErrorStatus.INVALID_NICKNAME_FORMAT);
        }
        if (!nickname.matches("^[a-zA-Z0-9가-힣]+$")) {
            throw new MemberHandler(ErrorStatus.INVALID_NICKNAME_FORMAT);
        }
    }

    private void validateEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (!email.matches(emailPattern)) {
            throw new MemberHandler(ErrorStatus.INVALID_EMAIL_FORMAT);
        }
    }

    private void validatePhoneNumber(String phoneNumber) {
        if (!phoneNumber.matches("^010-\\d{4}-\\d{4}$")) {
            throw new MemberHandler(ErrorStatus.INVALID_PHONE_FORMAT);
        }
    }

    private void validatePasswordComplexity(String password) {
        if (password.length() < 8 || password.length() > 15) {
            throw new MemberHandler(ErrorStatus.PASSWORD_COMPLEXITY_FAIL);
        }
        String pattern = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).+$";
        if (!password.matches(pattern)) {
            throw new MemberHandler(ErrorStatus.PASSWORD_COMPLEXITY_FAIL);
        }
    }
}
