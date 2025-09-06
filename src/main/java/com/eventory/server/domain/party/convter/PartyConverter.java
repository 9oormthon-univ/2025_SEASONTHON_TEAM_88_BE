package com.eventory.server.domain.party.convter;

import com.eventory.server.domain.member.entity.Member;
import com.eventory.server.domain.party.dto.PartyRequestDTO;
import com.eventory.server.domain.party.dto.PartyResponseDTO;
import com.eventory.server.domain.party.entity.Party;
import com.eventory.server.domain.party.entity.Todo;
import com.eventory.server.domain.product.entity.Product;

public class PartyConverter {

    public static Party toParty(Member member, PartyRequestDTO.createPartyRequest request){
        return Party.builder()
                .name(request.getPackageName())
                .member(member)
                .build();
    }

    public static Todo toTodo(Party party, Product product){
        return Todo.builder()
                .product(product)
                .party(party)
                .task(product.getProductName()+" 구매하기")
                .build();
    }

    public static PartyResponseDTO.ProductInfo toProductInfo(Product product){
        return PartyResponseDTO.ProductInfo.builder()
                .productId(product.getId())
                .productName(product.getProductName())
                .productImage(product.getProductImages().isEmpty() ?
                        "https://goorm-eventory-bucket.s3.ap-northeast-2.amazonaws.com/product/Frame+1321318195+(1).png"
                        : product.getProductImages().get(0).getImageUrl())
                .category(product.getCategory())
                .price(product.getPrice())
                .build();
    }
}
