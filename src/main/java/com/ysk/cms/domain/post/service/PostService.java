package com.ysk.cms.domain.post.service;

import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.board.entity.Board;
import com.ysk.cms.domain.board.repository.BoardRepository;
import com.ysk.cms.domain.post.dto.*;
import com.ysk.cms.domain.post.entity.Post;
import com.ysk.cms.domain.post.entity.PostStatus;
import com.ysk.cms.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final BoardRepository boardRepository;

    public PageResponse<PostListDto> getPosts(String siteCode, String boardCode, Pageable pageable) {
        Page<Post> postPage = postRepository.findBySiteCodeAndBoardCode(siteCode, boardCode, pageable);
        return PageResponse.of(postPage.map(PostListDto::from));
    }

    public PageResponse<PostListDto> getPostsByStatus(String siteCode, String boardCode, PostStatus status, Pageable pageable) {
        Page<Post> postPage = postRepository.findBySiteCodeAndBoardCodeAndStatus(siteCode, boardCode, status, pageable);
        return PageResponse.of(postPage.map(PostListDto::from));
    }

    public PageResponse<PostListDto> getPinnedPosts(String siteCode, String boardCode, Pageable pageable) {
        Page<Post> postPage = postRepository.findPinnedPosts(siteCode, boardCode, pageable);
        return PageResponse.of(postPage.map(PostListDto::from));
    }

    public PageResponse<PostListDto> searchPosts(String siteCode, String boardCode, String keyword, Pageable pageable) {
        Page<Post> postPage = postRepository.searchByKeyword(siteCode, boardCode, keyword, pageable);
        return PageResponse.of(postPage.map(PostListDto::from));
    }

    public PostDto getPost(String siteCode, String boardCode, Long postId) {
        Post post = postRepository.findByIdAndSiteCodeAndBoardCode(postId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        return PostDto.from(post);
    }

    @Transactional
    public PostDto getPostAndIncrementView(String siteCode, String boardCode, Long postId) {
        Post post = postRepository.findByIdAndSiteCodeAndBoardCode(postId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));
        post.incrementViewCount();
        return PostDto.from(post);
    }

    @Transactional
    public PostDto createPost(String siteCode, String boardCode, PostCreateRequest request) {
        Board board = boardRepository.findBySiteCodeAndCode(siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        Post post = Post.builder()
                .board(board)
                .title(request.getTitle())
                .content(request.getContent())
                .author(request.getAuthor())
                .isPinned(request.getIsPinned())
                .isSecret(request.getIsSecret())
                .status(request.getStatus())
                .build();

        if (request.getStatus() == PostStatus.PUBLISHED) {
            post.publish();
        }

        Post savedPost = postRepository.save(post);
        return PostDto.from(savedPost);
    }

    @Transactional
    public PostDto updatePost(String siteCode, String boardCode, Long postId, PostUpdateRequest request) {
        Post post = postRepository.findByIdAndSiteCodeAndBoardCode(postId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        post.update(
                request.getTitle(),
                request.getContent(),
                request.getAuthor(),
                request.getIsPinned(),
                request.getIsSecret(),
                request.getStatus(),
                request.getAnswer()
        );

        return PostDto.from(post);
    }

    @Transactional
    public void deletePost(String siteCode, String boardCode, Long postId) {
        Post post = postRepository.findByIdAndSiteCodeAndBoardCode(postId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        post.delete();
    }

    @Transactional
    public PostDto publishPost(String siteCode, String boardCode, Long postId) {
        Post post = postRepository.findByIdAndSiteCodeAndBoardCode(postId, siteCode, boardCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

        post.publish();
        return PostDto.from(post);
    }
}
