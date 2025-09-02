package com.eventory.server.domain.member.repository;

import com.eventory.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    boolean existsByNickname(String nickname);
}
