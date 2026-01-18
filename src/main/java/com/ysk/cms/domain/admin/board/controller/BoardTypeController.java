package com.ysk.cms.domain.admin.board.controller;

import com.ysk.cms.common.dto.ApiResponse;
import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.domain.admin.board.dto.BoardTypeCreateRequest;
import com.ysk.cms.domain.admin.board.dto.BoardTypeDto;
import com.ysk.cms.domain.admin.board.dto.BoardTypeUpdateRequest;
import com.ysk.cms.domain.admin.board.service.BoardTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "게시판 타입 관리", description = "게시판 타입 생성, 수정, 삭제")
@RestController
@RequestMapping("/api/sites/{siteCode}/board-types")
@RequiredArgsConstructor
public class BoardTypeController {

    private final BoardTypeService boardTypeService;

    @Operation(summary = "게시판 타입 목록 조회 (페이징)")
    @GetMapping("/paged")
    public ApiResponse<PageResponse<BoardTypeDto>> getBoardTypesPaged(
            @PathVariable String siteCode,
            @PageableDefault(size = 20, sort = "sortOrder", direction = Sort.Direction.ASC) Pageable pageable) {
        return ApiResponse.success(boardTypeService.getBoardTypesBySite(siteCode, pageable));
    }

    @Operation(summary = "게시판 타입 전체 목록 조회")
    @GetMapping
    public ApiResponse<List<BoardTypeDto>> getBoardTypes(@PathVariable String siteCode) {
        return ApiResponse.success(boardTypeService.getBoardTypesBySite(siteCode));
    }

    @Operation(summary = "활성 게시판 타입 목록 조회")
    @GetMapping("/active")
    public ApiResponse<List<BoardTypeDto>> getActiveBoardTypes(@PathVariable String siteCode) {
        return ApiResponse.success(boardTypeService.getActiveBoardTypes(siteCode));
    }

    @Operation(summary = "게시판 타입 상세 조회")
    @GetMapping("/{typeCode}")
    public ApiResponse<BoardTypeDto> getBoardType(
            @PathVariable String siteCode,
            @PathVariable String typeCode) {
        return ApiResponse.success(boardTypeService.getBoardType(siteCode, typeCode));
    }

    @Operation(summary = "게시판 타입 생성")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<BoardTypeDto> createBoardType(
            @PathVariable String siteCode,
            @Valid @RequestBody BoardTypeCreateRequest request) {
        return ApiResponse.success(boardTypeService.createBoardType(siteCode, request));
    }

    @Operation(summary = "게시판 타입 수정")
    @PutMapping("/{typeCode}")
    public ApiResponse<BoardTypeDto> updateBoardType(
            @PathVariable String siteCode,
            @PathVariable String typeCode,
            @Valid @RequestBody BoardTypeUpdateRequest request) {
        return ApiResponse.success(boardTypeService.updateBoardType(siteCode, typeCode, request));
    }

    @Operation(summary = "게시판 타입 삭제")
    @DeleteMapping("/{typeCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteBoardType(
            @PathVariable String siteCode,
            @PathVariable String typeCode) {
        boardTypeService.deleteBoardType(siteCode, typeCode);
        return ApiResponse.success(null);
    }
}
