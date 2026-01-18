package com.ysk.cms.domain.admin.board.service;

import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.admin.board.dto.BoardTypeCreateRequest;
import com.ysk.cms.domain.admin.board.dto.BoardTypeDto;
import com.ysk.cms.domain.admin.board.dto.BoardTypeUpdateRequest;
import com.ysk.cms.domain.admin.board.entity.BoardTypeEntity;
import com.ysk.cms.domain.admin.board.entity.BoardTypeStatus;
import com.ysk.cms.domain.admin.board.repository.BoardTypeRepository;
import com.ysk.cms.domain.admin.site.entity.Site;
import com.ysk.cms.domain.admin.site.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardTypeService {

    private final BoardTypeRepository boardTypeRepository;
    private final SiteRepository siteRepository;

    public List<BoardTypeDto> getBoardTypesBySite(String siteCode) {
        return boardTypeRepository.findBySiteCode(siteCode).stream()
                .map(BoardTypeDto::from)
                .toList();
    }

    public PageResponse<BoardTypeDto> getBoardTypesBySite(String siteCode, Pageable pageable) {
        Page<BoardTypeEntity> boardTypePage = boardTypeRepository.findBySiteCode(siteCode, pageable);
        return PageResponse.of(boardTypePage.map(BoardTypeDto::from));
    }

    public BoardTypeDto getBoardType(String siteCode, String typeCode) {
        BoardTypeEntity boardType = boardTypeRepository.findBySiteCodeAndCode(siteCode, typeCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_TYPE_NOT_FOUND));
        return BoardTypeDto.from(boardType);
    }

    public List<BoardTypeDto> getActiveBoardTypes(String siteCode) {
        return boardTypeRepository.findBySiteCodeAndStatus(siteCode, BoardTypeStatus.ACTIVE).stream()
                .map(BoardTypeDto::from)
                .toList();
    }

    @Transactional
    public BoardTypeDto createBoardType(String siteCode, BoardTypeCreateRequest request) {
        Site site = siteRepository.findByCode(siteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.SITE_NOT_FOUND));

        if (boardTypeRepository.existsBySiteCodeAndCode(siteCode, request.getCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_BOARD_TYPE_CODE);
        }

        BoardTypeEntity boardType = BoardTypeEntity.builder()
                .site(site)
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .icon(request.getIcon())
                .color(request.getColor())
                .bgColor(request.getBgColor())
                .sortOrder(request.getSortOrder())
                .build();

        BoardTypeEntity savedBoardType = boardTypeRepository.save(boardType);
        return BoardTypeDto.from(savedBoardType);
    }

    @Transactional
    public BoardTypeDto updateBoardType(String siteCode, String typeCode, BoardTypeUpdateRequest request) {
        BoardTypeEntity boardType = boardTypeRepository.findBySiteCodeAndCode(siteCode, typeCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_TYPE_NOT_FOUND));

        boardType.update(
                request.getName() != null ? request.getName() : boardType.getName(),
                request.getDescription() != null ? request.getDescription() : boardType.getDescription(),
                request.getIcon() != null ? request.getIcon() : boardType.getIcon(),
                request.getColor() != null ? request.getColor() : boardType.getColor(),
                request.getBgColor() != null ? request.getBgColor() : boardType.getBgColor(),
                request.getSortOrder() != null ? request.getSortOrder() : boardType.getSortOrder(),
                request.getStatus() != null ? request.getStatus() : boardType.getStatus()
        );

        return BoardTypeDto.from(boardType);
    }

    @Transactional
    public void deleteBoardType(String siteCode, String typeCode) {
        BoardTypeEntity boardType = boardTypeRepository.findBySiteCodeAndCode(siteCode, typeCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_TYPE_NOT_FOUND));

        boardType.delete();
    }
}
