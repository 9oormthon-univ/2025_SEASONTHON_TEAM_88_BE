package com.eventory.server.domain.party.dto;

import com.eventory.server.domain.party.entity.enums.ExpectedRange;
import com.eventory.server.domain.party.entity.enums.ParticipantType;
import com.eventory.server.domain.party.entity.enums.Purpose;
import com.eventory.server.domain.product.entity.enums.Category;
import lombok.Getter;

import java.util.List;

public class PartyRequestDTO {

    @Getter
    public static class PartySurveyRequest {
        private Purpose partyPurpose;
        private ExpectedRange budgetRange;
        private ParticipantType companionType;
        private String preparationContent;
        private List<Category> wishListItems;
    }
}
