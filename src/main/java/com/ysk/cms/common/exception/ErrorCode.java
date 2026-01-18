package com.ysk.cms.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "서버 내부 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "허용되지 않은 메서드입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "리소스를 찾을 수 없습니다."),

    // Auth
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "A002", "접근 권한이 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "만료된 토큰입니다."),
    LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "A005", "아이디 또는 비밀번호가 올바르지 않습니다."),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "U002", "이미 존재하는 사용자명입니다."),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U003", "이미 존재하는 이메일입니다."),
    USER_LOCKED(HttpStatus.FORBIDDEN, "U004", "잠긴 계정입니다."),
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "U005", "역할을 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U006", "현재 비밀번호가 일치하지 않습니다."),
    USER_PENDING(HttpStatus.FORBIDDEN, "U007", "승인 대기 중인 계정입니다. 관리자 승인 후 로그인이 가능합니다."),
    USER_INACTIVE(HttpStatus.FORBIDDEN, "U008", "비활성화된 계정입니다."),
    USER_SUSPENDED(HttpStatus.FORBIDDEN, "U009", "정지된 계정입니다."),

    // Site
    SITE_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "사이트를 찾을 수 없습니다."),
    DUPLICATE_SITE_CODE(HttpStatus.CONFLICT, "S002", "이미 존재하는 사이트 코드입니다."),
    SITE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "S003", "해당 사이트에 접근 권한이 없습니다."),

    // Board
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "B001", "게시판을 찾을 수 없습니다."),
    DUPLICATE_BOARD_CODE(HttpStatus.CONFLICT, "B002", "이미 존재하는 게시판 코드입니다."),

    // BoardType
    BOARD_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "BT001", "게시판 타입을 찾을 수 없습니다."),
    DUPLICATE_BOARD_TYPE_CODE(HttpStatus.CONFLICT, "BT002", "이미 존재하는 게시판 타입 코드입니다."),

    // Article (게시글)
    ARTICLE_NOT_FOUND(HttpStatus.NOT_FOUND, "AR001", "게시글을 찾을 수 없습니다."),

    // Reply (댓글)
    REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "RP001", "댓글을 찾을 수 없습니다."),
    PARENT_REPLY_NOT_FOUND(HttpStatus.NOT_FOUND, "RP002", "상위 댓글을 찾을 수 없습니다."),
    COMMENT_DISABLED(HttpStatus.FORBIDDEN, "RP003", "이 게시판에서는 댓글 기능이 비활성화되어 있습니다."),

    // Contents (정적 페이지)
    CONTENTS_NOT_FOUND(HttpStatus.NOT_FOUND, "CT001", "컨텐츠를 찾을 수 없습니다."),
    DUPLICATE_CONTENTS_SLUG(HttpStatus.CONFLICT, "CT002", "이미 존재하는 컨텐츠 슬러그입니다."),

    // AtchFile (첨부파일)
    FILE_NOT_FOUND(HttpStatus.NOT_FOUND, "AF001", "첨부파일을 찾을 수 없습니다."),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AF002", "파일 업로드에 실패했습니다."),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "AF003", "허용되지 않은 파일 형식입니다."),
    FILE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "AF004", "파일 크기가 초과되었습니다."),

    // Menu
    MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "MN001", "메뉴를 찾을 수 없습니다."),
    DUPLICATE_MENU_CODE(HttpStatus.CONFLICT, "MN002", "이미 존재하는 메뉴 코드입니다."),
    INVALID_MENU_PARENT(HttpStatus.BAD_REQUEST, "MN003", "자기 자신을 상위 메뉴로 지정할 수 없습니다."),
    CIRCULAR_MENU_REFERENCE(HttpStatus.BAD_REQUEST, "MN004", "메뉴 순환 참조가 발생했습니다."),

    // Role
    DUPLICATE_ROLE_NAME(HttpStatus.CONFLICT, "R001", "이미 존재하는 역할명입니다."),
    SYSTEM_ROLE_NOT_DELETABLE(HttpStatus.FORBIDDEN, "R002", "시스템 기본 역할은 삭제할 수 없습니다."),

    // UserMenu (사용자 메뉴)
    USER_MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "UM001", "사용자 메뉴를 찾을 수 없습니다."),
    DUPLICATE_USER_MENU_CODE(HttpStatus.CONFLICT, "UM002", "이미 존재하는 사용자 메뉴 코드입니다."),
    INVALID_USER_MENU_PARENT(HttpStatus.BAD_REQUEST, "UM003", "자기 자신을 상위 메뉴로 지정할 수 없습니다."),
    CIRCULAR_USER_MENU_REFERENCE(HttpStatus.BAD_REQUEST, "UM004", "메뉴 순환 참조가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
