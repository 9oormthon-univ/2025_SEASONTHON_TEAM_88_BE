package com.eventory.server.domain.member.entity;

import com.eventory.server.domain.member.entity.enums.Provider;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginInfo {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Provider provider;

    @Column(length = 255, unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 20)
    private String tel;

    @Column(length = 255)
    private String email;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "loginInfo")
    private Member member;

    public void encodePassword(String password) {
        this.password = password;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateTel(String tel) {
        this.tel = tel;
    }

    public void updateEmail(String email) {
        this.email = email;
    }
}
