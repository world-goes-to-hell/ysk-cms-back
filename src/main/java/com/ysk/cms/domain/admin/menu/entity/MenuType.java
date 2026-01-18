package com.ysk.cms.domain.admin.menu.entity;

public enum MenuType {
    DIRECTORY,      // 디렉토리 (하위 메뉴를 가지는 폴더)
    INTERNAL,       // 내부 링크
    EXTERNAL,       // 외부 링크
    BOARD,          // 게시판 연결
    PAGE            // 페이지 연결
}
