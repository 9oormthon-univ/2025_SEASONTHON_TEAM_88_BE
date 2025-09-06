package com.eventory.server.domain.party.repository;

import com.eventory.server.domain.party.entity.Party;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, Long> {
}
