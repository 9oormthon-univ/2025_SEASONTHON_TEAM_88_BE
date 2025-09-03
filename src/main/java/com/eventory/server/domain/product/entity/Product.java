package com.eventory.server.domain.product.entity;

import com.eventory.server.domain.product.entity.enums.Category;
import com.eventory.server.domain.product.entity.enums.Delivery;
import com.eventory.server.domain.review.entity.Review;
import com.eventory.server.domain.common.BaseEntity;
import com.eventory.server.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "price")
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_type")
    private Delivery deliveryType;

    @Column(name = "discount_percentage")
    private Integer discountPercentage;

    @Column(name = "point")
    private Integer point;

    @Column(name = "like_count")
    private Integer likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductImage> productImages = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();
}
