package com.eventory.server.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

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

    @Getter
    @Setter
    public static class LoginDTO{
        @NotBlank(message = "아이디는 필수입니다.")
        private String username;

        @NotBlank(message = "패스워드는 필수입니다.")
        private String password;
    }
}
