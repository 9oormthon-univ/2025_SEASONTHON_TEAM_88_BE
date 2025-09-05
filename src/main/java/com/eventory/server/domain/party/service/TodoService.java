package com.eventory.server.domain.party.service;

import com.eventory.server.domain.party.dto.request.CreateTodoRequest;
import com.eventory.server.domain.party.dto.response.CreateTodoResponse;
import com.eventory.server.domain.party.entity.Party;
import com.eventory.server.domain.party.entity.Todo;
import com.eventory.server.domain.party.repository.PartyRepository;
import com.eventory.server.domain.party.repository.TodoRepository;
import com.eventory.server.global.apipayload.code.status.ErrorStatus;
import com.eventory.server.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final PartyRepository partyRepository;

    @Transactional
    public CreateTodoResponse createTodo(Long userId, CreateTodoRequest createTodoRequest) {
        Long partyId = createTodoRequest.partyId();
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTY_NOT_FOUND));

        if (!party.getMember().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN_TODO_CREATE);
        }

        String task = createTodoRequest.task();

        Todo todo = Todo.builder()
                .party(party)
                .product(null)
                .task(task)
                .build();

        todoRepository.save(todo);
        return new CreateTodoResponse(partyId, todo.getId(), task);
    }
}
