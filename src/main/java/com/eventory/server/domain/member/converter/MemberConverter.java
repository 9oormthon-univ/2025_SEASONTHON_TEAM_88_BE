package com.eventory.server.domain.member.converter;

import com.eventory.server.domain.member.dto.MemberRequestDTO;
import com.eventory.server.domain.member.entity.LoginInfo;
import com.eventory.server.domain.member.entity.Member;
import com.eventory.server.domain.member.entity.enums.Provider;

public class MemberConverter {

    public static Member toMember(MemberRequestDTO.joinDTO request){
        Member member = Member.builder()
                .nickname(request.getNickname())
                .build();
        return member;
    }

    public static LoginInfo toLocalLoginInfo(MemberRequestDTO.joinDTO request, Member member){
        return LoginInfo.builder()
                .username(request.getUsername())
                .password(request.getPassword1())
                .email(request.getEmail())
                .tel(request.getPhoneNumber())
                .provider(Provider.LOCAL)
                .member(member)
                .build();
    }
}
