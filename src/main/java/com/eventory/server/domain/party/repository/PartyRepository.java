package com.eventory.server.domain.party.repository;

import com.eventory.server.domain.party.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartyRepository extends JpaRepository<Party, Long> {
    List<Party> findByMemberId(Long memberId);

    Boolean existsByMemberId(Long memberId);
}
