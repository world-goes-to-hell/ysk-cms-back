package com.ysk.cms.domain.board.service;

import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.board.dto.BoardCreateRequest;
import com.ysk.cms.domain.board.dto.BoardDto;
import com.ysk.cms.domain.board.dto.BoardUpdateRequest;
import com.ysk.cms.domain.board.entity.Board;
import com.ysk.cms.domain.board.entity.BoardStatus;
import com.ysk.cms.domain.board.repository.BoardRepository;
import com.ysk.cms.domain.site.entity.Site;
import com.ysk.cms.domain.site.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final SiteRepository siteRepository;

    public List<BoardDto> getBoardsBySite(String siteCode) {
        return boardRepository.findBySiteCode(siteCode).stream()
                .map(BoardDto::from)
                .toList();
    }

    public PageResponse<BoardDto> getBoardsBySite(String siteCode, Pageable pageable) {
        Page<Board> boardPage = boardRepository.findBySiteCode(siteCode, pageable);
        return PageResponse.of(boardPage.map(BoardDto::from));
    }

    public BoardDto getBoard(String siteCode, String boardCode) {
        Board board = boardRepository.findBySiteCodeAndCode(siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));
        return BoardDto.from(board);
    }

    public List<BoardDto> getActiveBoards(String siteCode) {
        return boardRepository.findBySiteCodeAndStatus(siteCode, BoardStatus.ACTIVE).stream()
                .map(BoardDto::from)
                .toList();
    }

    @Transactional
    public BoardDto createBoard(String siteCode, BoardCreateRequest request) {
        Site site = siteRepository.findByCode(siteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.SITE_NOT_FOUND));

        if (boardRepository.existsBySiteCodeAndCode(siteCode, request.getCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_BOARD_CODE);
        }

        Board board = Board.builder()
                .site(site)
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .typeCode(request.getTypeCode())
                .useComment(request.getUseComment())
                .useAttachment(request.getUseAttachment())
                .attachmentLimit(request.getAttachmentLimit())
                .useSecret(request.getUseSecret())
                .usePinned(request.getUsePinned())
                .sortOrder(request.getSortOrder())
                .build();

        Board savedBoard = boardRepository.save(board);
        return BoardDto.from(savedBoard);
    }

    @Transactional
    public BoardDto updateBoard(String siteCode, String boardCode, BoardUpdateRequest request) {
        Board board = boardRepository.findBySiteCodeAndCode(siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        board.update(
                request.getName(),
                request.getDescription(),
                request.getTypeCode() != null ? request.getTypeCode() : board.getTypeCode(),
                request.getUseComment(),
                request.getUseAttachment(),
                request.getAttachmentLimit(),
                request.getUseSecret(),
                request.getUsePinned(),
                request.getSortOrder(),
                request.getStatus()
        );

        return BoardDto.from(board);
    }

    @Transactional
    public void deleteBoard(String siteCode, String boardCode) {
        Board board = boardRepository.findBySiteCodeAndCode(siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        board.delete();
    }
}
