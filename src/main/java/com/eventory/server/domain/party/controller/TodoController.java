package com.eventory.server.domain.party.controller;

import com.eventory.server.domain.party.dto.request.CreateTodoRequest;
import com.eventory.server.domain.party.dto.request.TodoCompleteRequest;
import com.eventory.server.domain.party.dto.response.CreateTodoResponse;
import com.eventory.server.domain.party.dto.response.DeleteTodoResponse;
import com.eventory.server.domain.party.dto.response.TodoCompleteResponse;
import com.eventory.server.domain.party.service.TodoService;
import com.eventory.server.global.apipayload.ApiResponse;
import com.eventory.server.global.apipayload.code.status.SuccessStatus;
import com.eventory.server.global.security.handler.AuthUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @Operation(summary = "Todo 생성 API")
    @PostMapping
    public ApiResponse<CreateTodoResponse> createTodo(
            @AuthUser Long userId,
            @RequestBody CreateTodoRequest createTodoRequest
    ) {
        CreateTodoResponse createTodoResponse = todoService.createTodo(userId, createTodoRequest);
        return ApiResponse.of(SuccessStatus.TODO_CREATE_OK, createTodoResponse);
    }

    @Operation(summary = "Todo 진행 상태 변경 API")
    @PatchMapping("/{todoId}")
    public ApiResponse<TodoCompleteResponse> updateTodoComplete(
            @AuthUser Long memberId,
            @PathVariable(name = "todoId") Long todoId,
            @RequestBody TodoCompleteRequest todoCompleteRequest
    ) {
        TodoCompleteResponse todoCompleteResponse = todoService.updateTodoComplete(memberId, todoId, todoCompleteRequest);
        return ApiResponse.of(SuccessStatus.TODO_STATUS_UPDATE_OK, todoCompleteResponse);
    }

    @Operation(summary = "Todo 삭제 API")
    @DeleteMapping("/{todoId}")
    public ApiResponse<DeleteTodoResponse> deleteTodo(
            @AuthUser Long memberId,
            @PathVariable(name = "todoId") Long todoId
    ) {
        DeleteTodoResponse deleteTodoResponse = todoService.deleteTodo(memberId, todoId);
        return ApiResponse.of(SuccessStatus.TODO_DELETE_OK, deleteTodoResponse);
    }
}
