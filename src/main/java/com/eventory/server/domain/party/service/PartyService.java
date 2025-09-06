package com.eventory.server.domain.party.service;

import com.eventory.server.domain.party.dto.response.DeletePartyResponse;
import com.eventory.server.domain.party.dto.response.main.MyPartyResponse;
import com.eventory.server.domain.party.dto.response.main.TodoResponse;
import com.eventory.server.domain.party.entity.Party;
import com.eventory.server.domain.party.entity.Todo;
import com.eventory.server.domain.party.entity.enums.TodoType;
import com.eventory.server.domain.party.repository.PartyRepository;
import com.eventory.server.domain.party.repository.TodoRepository;
import com.eventory.server.global.apipayload.code.status.ErrorStatus;
import com.eventory.server.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository partyRepository;
    private final TodoRepository todoRepository;

    public MyPartyResponse partyMain(Long memberId) {
        // Todo progressRate 기능 구현
        List<Party> myParties = partyRepository.findByMemberId(memberId);
        if (myParties.isEmpty()) {
            return null;
        }
        MyPartyResponse myPartyResponse = null;
        TodoType todoType;
        for (Party myParty : myParties) {
            Long partyId = myParty.getId();
            String partyName = myParty.getName();
            Double progressRate = 0D;
            List<Todo> todos = todoRepository.findByPartyId(partyId);
            List<TodoResponse> todoResponses = new ArrayList<>();
            TodoResponse todoResponse;
            for (Todo todo : todos) {
                Long todoId = todo.getId();
                Long productId = null;
                String productName = null;
                String task = null;
                Boolean isCompleted = todo.getIsCompleted();
                if (todo.getProduct() != null) {
                    todoType = TodoType.PRODUCT;
                    productId = todo.getProduct().getId();
                    productName = todo.getProduct().getProductName();
                    todoResponse = new TodoResponse(todoId, todoType, productId, productName, task, isCompleted);
                } else if (todo.getTask() != null){
                    todoType = TodoType.CUSTOM;
                    task = todo.getTask();
                    todoResponse = new TodoResponse(todoId, todoType, productId, productName, task, isCompleted);
                } else {
                    todoResponse = null;
                }
                todoResponses.add(todoResponse);
            }
            myPartyResponse = new MyPartyResponse(partyId, partyName, progressRate, todoResponses);
        }
        return myPartyResponse;
    }

    /**
     * 내 파티 삭제
     * @param userId
     * @param partyId
     * @return DeletePartyResponse (partyId)
     */
    @Transactional
    public DeletePartyResponse deleteParty(Long userId, Long partyId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTY_NOT_FOUND));

        if (!party.getMember().getId().equals(userId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN_PARTY_DELETE);
        }

        partyRepository.delete(party);

        return new DeletePartyResponse(party.getId());
    }
}
