package com.eventory.server.domain.like.repository;

import com.eventory.server.domain.like.entity.Like;
import com.eventory.server.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);

    @Query("SELECT l FROM Like l JOIN FETCH l.product WHERE l.member = :member")
    List<Like> findByMemberWithProduct(@Param("member") Member member);
}
