package com.eventory.server.domain.party.service;

import com.eventory.server.domain.member.entity.Member;
import com.eventory.server.domain.member.repository.MemberRepository;
import com.eventory.server.domain.party.dto.PartyRequestDTO;
import com.eventory.server.domain.party.entity.Party;
import com.eventory.server.domain.party.repository.PartyRepository;
import com.eventory.server.domain.party.repository.TodoRepository;
import com.eventory.server.domain.product.entity.Product;
import com.eventory.server.domain.product.repository.ProductRepository;
import com.eventory.server.global.apipayload.code.status.ErrorStatus;
import com.eventory.server.global.apipayload.exception.handler.MemberHandler;
import com.eventory.server.global.apipayload.exception.handler.ProductHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.eventory.server.domain.party.convter.PartyConverter.toParty;
import static com.eventory.server.domain.party.convter.PartyConverter.toTodo;

@Service
@RequiredArgsConstructor
public class PartyCommandService {

    private final MemberRepository memberRepository;
    private final PartyRepository partyRepository;
    private final ProductRepository productRepository;
    private final TodoRepository todoRepository;

    public void createParty(Long memberId, PartyRequestDTO.createPartyRequest request){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Party party = toParty(member, request);
        partyRepository.save(party);

        for (Long productId : request.getWishListItems()) {
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new ProductHandler(ErrorStatus.PRODUCT_NOT_FOUND));
            todoRepository.save(toTodo(party, product));
        }
    }
}