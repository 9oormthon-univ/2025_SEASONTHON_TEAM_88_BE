package com.eventory.server.domain.member.repository;

import com.eventory.server.domain.member.entity.LoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LoginInfoRepository extends JpaRepository<LoginInfo, Long> {
    boolean existsByUsername(String username);
    
    @Query("SELECT l FROM LoginInfo l JOIN FETCH l.member WHERE l.username = :username")
    Optional<LoginInfo> findByUsernameWithUser(@Param("username") String username);
}
