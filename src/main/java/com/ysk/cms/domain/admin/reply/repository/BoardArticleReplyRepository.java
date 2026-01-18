package com.ysk.cms.domain.admin.reply.repository;

import com.ysk.cms.domain.admin.reply.entity.BoardArticleReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardArticleReplyRepository extends JpaRepository<BoardArticleReply, Long> {

    // 게시글의 모든 댓글 조회 (삭제되지 않은 것만, 부모 댓글만)
    @Query("SELECT r FROM BoardArticleReply r " +
           "WHERE r.article.id = :articleId " +
           "AND r.parent IS NULL " +
           "AND r.deleted = false " +
           "ORDER BY r.createdAt ASC")
    List<BoardArticleReply> findByArticleIdAndParentIsNull(@Param("articleId") Long articleId);

    // 게시글의 모든 댓글 페이징 조회 (부모 댓글만)
    @Query("SELECT r FROM BoardArticleReply r " +
           "WHERE r.article.id = :articleId " +
           "AND r.parent IS NULL " +
           "AND r.deleted = false")
    Page<BoardArticleReply> findByArticleIdAndParentIsNull(@Param("articleId") Long articleId, Pageable pageable);

    // 게시글의 모든 댓글 페이징 조회 (플랫 구조 - 부모+자식 모두, 삭제된 댓글 포함)
    // 정렬: path 기준 정렬 (대댓글 체인 유지)
    @Query(value = "SELECT r FROM BoardArticleReply r " +
           "WHERE r.article.id = :articleId " +
           "ORDER BY r.path ASC",
           countQuery = "SELECT COUNT(r) FROM BoardArticleReply r WHERE r.article.id = :articleId")
    Page<BoardArticleReply> findAllByArticleIdFlat(@Param("articleId") Long articleId, Pageable pageable);

    // 특정 댓글의 대댓글 조회
    @Query("SELECT r FROM BoardArticleReply r " +
           "WHERE r.parent.id = :parentId " +
           "AND r.deleted = false " +
           "ORDER BY r.createdAt ASC")
    List<BoardArticleReply> findByParentId(@Param("parentId") Long parentId);

    // 댓글 ID와 게시글 ID로 조회
    @Query("SELECT r FROM BoardArticleReply r " +
           "WHERE r.id = :replyId " +
           "AND r.article.id = :articleId " +
           "AND r.deleted = false")
    Optional<BoardArticleReply> findByIdAndArticleId(@Param("replyId") Long replyId, @Param("articleId") Long articleId);

    // 게시글의 댓글 수 조회
    @Query("SELECT COUNT(r) FROM BoardArticleReply r " +
           "WHERE r.article.id = :articleId " +
           "AND r.deleted = false")
    Long countByArticleId(@Param("articleId") Long articleId);

    // 사이트 코드와 게시판 코드로 댓글 조회
    @Query("SELECT r FROM BoardArticleReply r " +
           "JOIN r.article a " +
           "JOIN a.board b " +
           "JOIN b.site s " +
           "WHERE s.code = :siteCode " +
           "AND b.code = :boardCode " +
           "AND a.id = :articleId " +
           "AND r.id = :replyId " +
           "AND r.deleted = false")
    Optional<BoardArticleReply> findBySiteCodeAndBoardCodeAndArticleIdAndReplyId(
            @Param("siteCode") String siteCode,
            @Param("boardCode") String boardCode,
            @Param("articleId") Long articleId,
            @Param("replyId") Long replyId);
}
