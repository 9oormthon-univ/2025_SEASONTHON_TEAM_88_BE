package com.eventory.server.domain.member.converter;

import com.eventory.server.domain.member.dto.KakaoProfile;
import com.eventory.server.domain.member.dto.MemberRequestDTO;
import com.eventory.server.domain.member.dto.MemberResponseDTO;
import com.eventory.server.domain.member.entity.LoginInfo;
import com.eventory.server.domain.member.entity.Member;
import com.eventory.server.domain.member.entity.enums.Provider;

import java.util.UUID;

public class MemberConverter {

    public static Member toMember(MemberRequestDTO.joinDTO request){
        Member member = Member.builder()
                .nickname(request.getNickname())
                .build();
        return member;
    }

    public static LoginInfo toLocalLoginInfo(MemberRequestDTO.joinDTO request, Member member){
        LoginInfo loginInfo = LoginInfo.builder()
                .username(request.getUsername())
                .password(request.getPassword1())
                .email(request.getEmail())
                .tel(request.getPhoneNumber())
                .provider(Provider.LOCAL)
                .member(member)
                .build();
        
        member.updateLoginInfo(loginInfo);
        return loginInfo;
    }

    public static MemberResponseDTO.LoginResultDTO toLoginResultDTO(
            Long memberId, String accessToken, String refreshToken){
        return MemberResponseDTO.LoginResultDTO.builder()
                .memberId(memberId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public static Member kakaoToMember(KakaoProfile kakaoProfile){
        String email = kakaoProfile.getKakaoAccount().getEmail();
        String nickname = email.substring(0, email.indexOf('@'));
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);

        return Member.builder()
                .nickname("kakao#" + nickname + "_" + uniqueId)
                .build();
    }

    public static LoginInfo toKakaoLoginInfo(KakaoProfile kakaoProfile, Member member){
        return LoginInfo.builder()
                .username("KAKAO_" + kakaoProfile.getId())
                .password("")
                .email(kakaoProfile.getKakaoAccount().getEmail())
                .tel("")
                .provider(Provider.KAKAO)
                .member(member)
                .build();
    }
}
