package com.eventory.server.domain.party.controller;

import com.eventory.server.domain.party.dto.request.CreateTodoRequest;
import com.eventory.server.domain.party.dto.response.CreateTodoResponse;
import com.eventory.server.domain.party.service.TodoService;
import com.eventory.server.global.apipayload.ApiResponse;
import com.eventory.server.global.apipayload.code.status.SuccessStatus;
import com.eventory.server.global.security.handler.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ApiResponse<CreateTodoResponse> createTodo(
            @AuthUser Long userId,
            @RequestBody CreateTodoRequest createTodoRequest
    ) {
        CreateTodoResponse createTodoResponse = todoService.createTodo(userId, createTodoRequest);
        return ApiResponse.of(SuccessStatus.TODO_CREATE_OK, createTodoResponse);
    }
}
