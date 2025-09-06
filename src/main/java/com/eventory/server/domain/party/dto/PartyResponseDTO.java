package com.eventory.server.domain.party.dto;

import com.eventory.server.domain.product.entity.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class PartyResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomPackageResponse {
        private List<BudgetPackage> customPackage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BudgetPackage {
        private int budget;
        private List<ProductInfo> products;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductInfo {
        private String productImage;
        private String productName;
        private Category category;
        private int price;
    }
}
