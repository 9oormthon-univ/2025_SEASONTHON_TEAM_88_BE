package com.eventory.server.domain.like.service;

import com.eventory.server.domain.like.dto.response.RegisterLikeResponse;
import com.eventory.server.domain.like.dto.request.RegisterLikeRequest;
import com.eventory.server.domain.like.entity.Like;
import com.eventory.server.domain.like.repository.LikeRepository;
import com.eventory.server.domain.member.entity.Member;
import com.eventory.server.domain.member.repository.MemberRepository;
import com.eventory.server.domain.product.entity.Product;
import com.eventory.server.domain.product.repository.ProductRepository;
import com.eventory.server.global.apipayload.code.status.ErrorStatus;
import com.eventory.server.global.apipayload.exception.GeneralException;
import com.eventory.server.global.apipayload.exception.handler.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    /**
     * 찜 등록
     * @param memberId
     * @param registerLikeRequest
     * @return RegisterLikeResponse(productId, likeId)
     * @throws AuthException
     */
    @Transactional
    public RegisterLikeResponse registerLike(Long memberId, RegisterLikeRequest registerLikeRequest) throws AuthException {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthException((ErrorStatus.MEMBER_NOT_FOUND)));

        Long productId = registerLikeRequest.productId();
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new GeneralException(ErrorStatus.PRODUCT_NOT_FOUND));

        boolean isExistsLike = likeRepository.existsByMemberIdAndProductId(memberId, productId);
        if (isExistsLike) {
            throw new GeneralException(ErrorStatus.LIKE_ALREADY_EXISTS);
        }

        Like like = Like.builder()
                .member(member)
                .product(product)
                .build();

        Like savedLike = likeRepository.save(like);
        Long likeId = savedLike.getId();
        RegisterLikeResponse registerLikeResponse = new RegisterLikeResponse(productId, likeId);
        return registerLikeResponse;
    }
}
