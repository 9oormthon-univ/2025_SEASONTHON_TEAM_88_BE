package com.eventory.server.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class MemberRequestDTO {

    @Getter
    public static class joinDTO{

        @NotBlank
        String username;

        @NotBlank
        String nickname;

        @NotBlank
        String email;

        @NotBlank
        String phoneNumber;

        @NotBlank
        String password1;

        @NotBlank
        String password2;

    }
}
