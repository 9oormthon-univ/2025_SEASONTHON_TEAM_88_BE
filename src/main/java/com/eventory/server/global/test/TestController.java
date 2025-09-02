package com.eventory.server.global.test;

import com.eventory.server.global.apipayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/test")
@Tag(name = "Test")
public class TestController {

    @Operation(summary = "Test API")
    @GetMapping("/test")
    public ApiResponse<String> getResponse() {
        return ApiResponse.onSuccess("응답 통일 예시");
    }
}
