package com.eventory.server.domain.like.repository;

import com.eventory.server.domain.like.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}
