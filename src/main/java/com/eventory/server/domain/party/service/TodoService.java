package com.eventory.server.domain.party.service;

import com.eventory.server.domain.party.dto.request.CreateTodoRequest;
import com.eventory.server.domain.party.dto.request.TodoCompleteRequest;
import com.eventory.server.domain.party.dto.response.CreateTodoResponse;
import com.eventory.server.domain.party.dto.response.DeleteTodoResponse;
import com.eventory.server.domain.party.dto.response.TodoCompleteResponse;
import com.eventory.server.domain.party.entity.Party;
import com.eventory.server.domain.party.entity.Todo;
import com.eventory.server.domain.party.repository.PartyRepository;
import com.eventory.server.domain.party.repository.TodoRepository;
import com.eventory.server.domain.product.repository.ProductRepository;
import com.eventory.server.global.apipayload.code.status.ErrorStatus;
import com.eventory.server.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final PartyRepository partyRepository;
    private final ProductRepository productRepository;

    /**
     * 투두 리스트 항목 추가
     * @param memberId
     * @param createTodoRequest
     * @return createTodoRequest (partyId, task)
     */
    @Transactional
    public CreateTodoResponse createTodo(Long memberId, CreateTodoRequest createTodoRequest) {
        // Todo memberId 추가
        Long partyId = createTodoRequest.partyId();
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTY_NOT_FOUND));

        if (!party.getMember().getId().equals(memberId)) {
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
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.TODO_NOT_FOUND));

        if (!todo.getParty().getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN_TODO_STATUS_UPDATE);
        }

        Boolean completed = todoCompleteRequest.isCompleted();
        todo.updateStatus(!completed);

        return new TodoCompleteResponse(todo.getId(), todo.getIsCompleted());
    }

    /**
     * 투두 리스트 항목 삭제
     * @param memberId
     * @param todoId
     * @return
     */
    @Transactional
    public DeleteTodoResponse deleteTodo(Long memberId, Long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.TODO_NOT_FOUND));

        if (!todo.getParty().getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN_TODO_DELETE);
        }

        todoRepository.delete(todo);
        return new DeleteTodoResponse(todo.getId());
    }

    public Double calculateReviewRating(Long partyId) {
        List<Todo> todos = todoRepository.findByPartyId(partyId);
        int todosSize = todos.size();
        int completeCount = 0;
        for (Todo todo : todos) {
            if (todo.getIsCompleted()) {
                completeCount++;
            }
        }

        return Math.round(((double) completeCount / todosSize) * 1000) / 10.0;
    }
}
