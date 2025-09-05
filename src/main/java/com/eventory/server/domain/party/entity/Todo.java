package com.eventory.server.domain.party.entity;

import com.eventory.server.domain.common.BaseEntity;
import com.eventory.server.domain.product.entity.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "task")
    private String task;

    @Builder.Default
    @Column(name = "is_completed")
    private boolean isCompleted = false;
}