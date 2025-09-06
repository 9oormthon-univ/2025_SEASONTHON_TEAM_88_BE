package com.eventory.server.domain.party.dto.response.main;

import com.eventory.server.domain.party.entity.enums.TodoType;

public record TodoResponse(
        Long todoId,
        TodoType todoType,
        Long productId,
        String productName,
        String task,
        Boolean isCompleted
) {
}
