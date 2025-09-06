package com.eventory.server.domain.party.repository;

import com.eventory.server.domain.party.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
