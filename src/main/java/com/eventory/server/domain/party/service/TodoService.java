package com.eventory.server.domain.party.service;

import com.eventory.server.domain.party.dto.request.CreateTodoRequest;
import com.eventory.server.domain.party.dto.request.TodoCompleteRequest;
import com.eventory.server.domain.party.dto.response.CreateTodoResponse;
import com.eventory.server.domain.party.dto.response.TodoCompleteResponse;
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

    /**
     * 투두 리스트 항목 추가
     * @param userId
     * @param createTodoRequest
     * @return createTodoRequest (partyId, task)
     */
    @Transactional
    public CreateTodoResponse createTodo(Long userId, CreateTodoRequest createTodoRequest) {
        // Todo memberId 추가
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

    /**
     * 투두 리스트 항목 진행 상태 변경
     * @param memberId
     * @param todoId
     * @param todoCompleteRequest
     * @return todoCompleteRequest (todoId, isCompleted)
     */
    @Transactional
    public TodoCompleteResponse updateTodoComplete(Long memberId, Long todoId, TodoCompleteRequest todoCompleteRequest) {
        // Todo memberId 추가
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.TODO_NOT_FOUND));

        Boolean completed = todoCompleteRequest.isCompleted();
        todo.updateStatus(!completed);

        return new TodoCompleteResponse(todo.getId(), todo.isCompleted());
    }
}
