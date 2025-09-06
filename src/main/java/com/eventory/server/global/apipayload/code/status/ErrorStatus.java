package com.eventory.server.global.apipayload.code.status;

import com.eventory.server.global.apipayload.code.BaseErrorCode;
import com.eventory.server.global.apipayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    GEMINI_NOT_WORK(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_003", "Gemini 오류가 발생했습니다."),

    // Member 관련
    MEMBER_NOT_FOUND(HttpStatus.BAD_REQUEST, "MEMBER4001", "사용자가 없습니다."),
    NICKNAME_NOT_EXIST(HttpStatus.BAD_REQUEST, "MEMBER4002", "닉네임은 필수 입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "MEMBER4003", "비밀번호가 일치하지 않습니다."),
    PASSWORD_COMPLEXITY_FAIL(HttpStatus.BAD_REQUEST, "MEMBER4004", "비밀번호는 영문자, 숫자, 특수문자를 모두 포함해야 합니다."),
    MEMBER_NOT_FOUND_LOGIN(HttpStatus.NOT_FOUND, "MEMBER4005", "존재하지 않는 사용자입니다."),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "MEMBER4006", "이미 사용 중인 username입니다."),
    LOGIN_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4007", "사용자 로그인 정보가 존재하지 않습니다."),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER4008", "이미 사용 중인 nickname입니다."),
    CURRENT_USERNAME(HttpStatus.BAD_REQUEST, "MEMBER4009", "현재 사용 중인 아이디입니다."),
    CURRENT_NICKNAME(HttpStatus.BAD_REQUEST, "MEMBER4010", "현재 사용 중인 닉네임입니다."),
    INVALID_USERNAME_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER4011", "아이디는 6-10자의 영문, 숫자 조합이어야 합니다."),
    INVALID_NICKNAME_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER4012", "닉네임은 2-12자의 한글, 영문, 숫자만 사용 가능합니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER4013", "올바른 이메일 형식이 아닙니다."),
    INVALID_PHONE_FORMAT(HttpStatus.BAD_REQUEST, "MEMBER4014", "휴대폰 번호는 010-XXXX-XXXX 형식이어야 합니다."),

    // Auth 관련
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4001", "유효하지 않은 토큰입니다."),
    AUTH_EXTRACT_ERROR(HttpStatus.UNAUTHORIZED, "AUTH4002", "토큰 추출에 실패했습니다."),
    INVALID_REQUEST_INFO_KAKAO(HttpStatus.UNAUTHORIZED, "AUTH_007", "카카오 정보 불러오기에 실패하였습니다."),
    AUTH_INVALID_CODE(HttpStatus.UNAUTHORIZED, "", "코드가 유효하지 않습니다."),

    // Like 관련
    LIKE_ALREADY_EXISTS(HttpStatus.CONFLICT, "LIKE4001", "이미 찜을 누른 상품입니다."),

    // Product 관련
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "PRODUCT4001", "존재하지 않는 상품입니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
