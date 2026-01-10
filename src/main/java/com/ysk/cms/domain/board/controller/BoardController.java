package com.ysk.cms.domain.board.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.domain.board.dto.BoardCreateRequest;
import com.ysk.cms.domain.board.dto.BoardDto;
import com.ysk.cms.domain.board.dto.BoardUpdateRequest;
import com.ysk.cms.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시판 관리", description = "게시판 생성, 수정, 삭제")
@RestController
@RequestMapping("/api/sites/{siteCode}/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @Operation(summary = "게시판 목록 조회 (페이징)")
    @GetMapping("/paged")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<PageResponse<BoardDto>> getBoardsPaged(
            @PathVariable String siteCode,
            @PageableDefault(size = 20, sort = "sortOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.success(boardService.getBoardsBySite(siteCode, pageable));
    }

    @Operation(summary = "게시판 전체 목록 조회")
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<List<BoardDto>> getBoards(@PathVariable String siteCode) {
        return ApiResponse.success(boardService.getBoardsBySite(siteCode));
    }

    @Operation(summary = "활성 게시판 목록 조회")
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<List<BoardDto>> getActiveBoards(@PathVariable String siteCode) {
        return ApiResponse.success(boardService.getActiveBoards(siteCode));
    }

    @Operation(summary = "게시판 상세 조회")
    @GetMapping("/{boardCode}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN', 'EDITOR', 'VIEWER')")
    public ApiResponse<BoardDto> getBoard(
            @PathVariable String siteCode,
            @PathVariable String boardCode) {
        return ApiResponse.success(boardService.getBoard(siteCode, boardCode));
    }

    @Operation(summary = "게시판 생성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    public ApiResponse<BoardDto> createBoard(
            @PathVariable String siteCode,
            @Valid @RequestBody BoardCreateRequest request) {
        return ApiResponse.success(boardService.createBoard(siteCode, request));
    }

    @Operation(summary = "게시판 수정")
    @PutMapping("/{boardCode}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    public ApiResponse<BoardDto> updateBoard(
            @PathVariable String siteCode,
            @PathVariable String boardCode,
            @Valid @RequestBody BoardUpdateRequest request) {
        return ApiResponse.success(boardService.updateBoard(siteCode, boardCode, request));
    }

    @Operation(summary = "게시판 삭제")
    @DeleteMapping("/{boardCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'SITE_ADMIN')")
    public ApiResponse<Void> deleteBoard(
            @PathVariable String siteCode,
            @PathVariable String boardCode) {
        boardService.deleteBoard(siteCode, boardCode);
        return ApiResponse.success(null);
    }
}
