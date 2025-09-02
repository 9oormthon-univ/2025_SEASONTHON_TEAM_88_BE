package com.eventory.server.domain.party.entity;

import com.eventory.server.domain.Party.entity.enums.ExpectedRange;
import com.eventory.server.domain.Party.entity.enums.ParticipantType;
import com.eventory.server.domain.Party.entity.enums.Purpose;
import com.eventory.server.domain.common.BaseEntity;
import com.eventory.server.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Party extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Purpose purpose;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExpectedRange expectedRange;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParticipantType participantType;

    private String specialNotes;

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartyShoppingItem> partyShoppingItemList = new ArrayList<>();

    @OneToMany(mappedBy = "party", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Todo> todoList = new ArrayList<>();
}
