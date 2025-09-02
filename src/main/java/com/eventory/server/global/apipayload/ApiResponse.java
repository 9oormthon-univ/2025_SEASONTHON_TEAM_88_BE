package com.eventory.server.global.apipayload;

import com.eventory.server.global.apipayload.code.BaseCode;
import com.eventory.server.global.apipayload.code.status.SuccessStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess","code","message","timestamp","result"})
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final String code;
    private final String message;
    private final String timestamp;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T result;


    public static <T> ApiResponse<T> onSuccess(T result){
        String timestamp = LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString();
        return new ApiResponse<>(true, SuccessStatus._OK.getCode() , SuccessStatus._OK.getMessage(), timestamp, result);
    }

    public static <T> ApiResponse<T> of(BaseCode code, T result){
            String timestamp = LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString();
            return new ApiResponse<>(true, code.getReasonHttpStatus().getCode() , code.getReasonHttpStatus().getMessage(), timestamp, result);
    }

    public static <T> ApiResponse<T> onFailure(String code, String message, T data){
        String timestamp = LocalDateTime.now(ZoneId.of("Asia/Seoul")).toString();
        return new ApiResponse<>(false, code, message, timestamp, data);
    }
}