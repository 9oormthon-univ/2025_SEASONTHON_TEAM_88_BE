package com.eventory.server.domain.party.repository;

import com.eventory.server.domain.party.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByPartyId(Long partyId);
}
