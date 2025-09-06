package com.eventory.server.domain.party.service;

import com.eventory.server.domain.member.entity.Member;
import com.eventory.server.domain.member.repository.MemberRepository;
import com.eventory.server.domain.party.dto.request.CreatePartyRequest;
import com.eventory.server.domain.party.dto.request.SelectPartyRequest;
import com.eventory.server.domain.party.dto.response.CreatePartyResponse;
import com.eventory.server.domain.party.dto.response.DeletePartyResponse;
import com.eventory.server.domain.party.dto.response.SelectPartyResponse;
import com.eventory.server.domain.party.dto.response.SelectPartyListResponse;
import com.eventory.server.domain.party.dto.response.main.MyPartyResponse;
import com.eventory.server.domain.party.dto.response.main.TodoResponse;
import com.eventory.server.domain.party.entity.Party;
import com.eventory.server.domain.party.entity.Todo;
import com.eventory.server.domain.party.entity.enums.TodoType;
import com.eventory.server.domain.party.repository.PartyRepository;
import com.eventory.server.domain.party.repository.TodoRepository;
import com.eventory.server.domain.product.entity.Product;
import com.eventory.server.domain.product.repository.ProductRepository;
import com.eventory.server.global.apipayload.code.status.ErrorStatus;
import com.eventory.server.global.apipayload.exception.GeneralException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.error.Error;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartyService {

    private final PartyRepository partyRepository;
    private final TodoRepository todoRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final TodoService todoService;

    /**
     * 내 파티 리스트 조회
     *
     * @param memberId
     * @return MyPartyResponse (partyId, partyName, progressRate, todoResponses)
     */
    public MyPartyResponse partyMain(Long memberId) {
        List<Party> myParties = partyRepository.findByMemberId(memberId);
        if (myParties.isEmpty()) {
            return null;
        }
        MyPartyResponse myPartyResponse = null;
        TodoType todoType;
        for (Party myParty : myParties) {
            Long partyId = myParty.getId();
            String partyName = myParty.getName();
            Double progressRate = todoService.calculateReviewRating(partyId);
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
                } else if (todo.getTask() != null) {
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
     *
     * @param memberId
     * @param partyId
     * @return DeletePartyResponse (partyId)
     */
    @Transactional
    public DeletePartyResponse deleteParty(Long memberId, Long partyId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTY_NOT_FOUND));

        if (!party.getMember().getId().equals(memberId)) {
            throw new GeneralException(ErrorStatus.FORBIDDEN_PARTY_DELETE);
        }

        partyRepository.delete(party);

        return new DeletePartyResponse(party.getId());
    }

    /**
     * 내 파티 생성
     *
     * @param memberId
     * @param createPartyRequest
     * @return CreatePartyResponse(partyId, partyName)
     */
    @Transactional
    public CreatePartyResponse createParty(Long memberId, CreatePartyRequest createPartyRequest) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        String partyName = createPartyRequest.partyName();

        Party party = Party.builder()
                .name(partyName)
                .member(member)
                .build();

        return new CreatePartyResponse(party.getId(), party.getName());
    }

    public SelectPartyResponse selectParty(Long memberId, Long partyId, List<SelectPartyRequest> selectPartyRequests) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTY_NOT_FOUND));

        for (SelectPartyRequest selectPartyRequest : selectPartyRequests) {
            Long productId = selectPartyRequest.productId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));
            Todo todo = Todo.builder()
                    .party(party)
                    .product(product)
                    .build();

            todoRepository.save(todo);
        }

        return new SelectPartyResponse(partyId);
    }

    public List<SelectPartyListResponse> selectPartyList(Long memberId) {
        List<Party> parties = partyRepository.findByMemberId(memberId);

        List<SelectPartyListResponse> selectPartyListResponses = new ArrayList<>();
        SelectPartyListResponse selectPartyListResponse;
        for (Party party : parties) {
            Long partyId = party.getId();
            String partyName = party.getName();
            selectPartyListResponse = new SelectPartyListResponse(partyId, partyName);
            selectPartyListResponses.add(selectPartyListResponse);
        }

        return selectPartyListResponses;
    }

    public SelectPartyResponse selectPartyMe(Long memberId, Long partyId, List<SelectPartyRequest> selectPartyRequests) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.MEMBER_NOT_FOUND));

        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PARTY_NOT_FOUND));

        for (SelectPartyRequest selectPartyRequest : selectPartyRequests) {
            Long productId = selectPartyRequest.productId();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));
            Todo todo = Todo.builder()
                    .party(party)
                    .product(product)
                    .build();

            todoRepository.save(todo);
        }

        return new SelectPartyResponse(partyId);
    }
}
