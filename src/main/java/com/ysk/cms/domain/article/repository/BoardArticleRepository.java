package com.ysk.cms.domain.article.repository;

import com.ysk.cms.domain.article.entity.BoardArticle;
import com.ysk.cms.domain.article.entity.ArticleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BoardArticleRepository extends JpaRepository<BoardArticle, Long> {

    @Query("SELECT a FROM BoardArticle a WHERE a.board.id = :boardId AND a.deleted = false")
    Page<BoardArticle> findByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    @Query("SELECT a FROM BoardArticle a WHERE a.board.site.code = :siteCode AND a.board.code = :boardCode AND a.deleted = false")
    Page<BoardArticle> findBySiteCodeAndBoardCode(@Param("siteCode") String siteCode, @Param("boardCode") String boardCode, Pageable pageable);

    @Query("SELECT a FROM BoardArticle a WHERE a.board.site.code = :siteCode AND a.board.code = :boardCode AND a.status = :status AND a.deleted = false")
    Page<BoardArticle> findBySiteCodeAndBoardCodeAndStatus(
            @Param("siteCode") String siteCode,
            @Param("boardCode") String boardCode,
            @Param("status") ArticleStatus status,
            Pageable pageable);

    @Query("SELECT a FROM BoardArticle a WHERE a.id = :id AND a.board.site.code = :siteCode AND a.board.code = :boardCode AND a.deleted = false")
    Optional<BoardArticle> findByIdAndSiteCodeAndBoardCode(
            @Param("id") Long id,
            @Param("siteCode") String siteCode,
            @Param("boardCode") String boardCode);

    @Query("SELECT a FROM BoardArticle a WHERE a.board.site.code = :siteCode AND a.board.code = :boardCode AND a.isPinned = true AND a.status = 'PUBLISHED' AND a.deleted = false ORDER BY a.createdAt DESC")
    Page<BoardArticle> findPinnedArticles(@Param("siteCode") String siteCode, @Param("boardCode") String boardCode, Pageable pageable);

    @Query("SELECT COUNT(a) FROM BoardArticle a WHERE a.board.id = :boardId AND a.deleted = false")
    long countByBoardId(@Param("boardId") Long boardId);

    @Query("""
            SELECT a FROM BoardArticle a
            WHERE a.board.site.code = :siteCode
            AND a.board.code = :boardCode
            AND a.deleted = false
            AND (a.title LIKE %:keyword% OR a.content LIKE %:keyword%)
            """)
    Page<BoardArticle> searchByKeyword(
            @Param("siteCode") String siteCode,
            @Param("boardCode") String boardCode,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Dashboard queries
    @Query("SELECT COUNT(a) FROM BoardArticle a WHERE a.deleted = false")
    long countAllActive();

    @Query("SELECT COUNT(a) FROM BoardArticle a WHERE a.deleted = false AND a.createdAt >= :startDate")
    long countArticlesCreatedSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(a) FROM BoardArticle a WHERE a.deleted = false AND a.createdAt >= :startDate AND a.createdAt < :endDate")
    long countArticlesCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT a FROM BoardArticle a JOIN FETCH a.board WHERE a.deleted = false ORDER BY a.createdAt DESC")
    List<BoardArticle> findRecentArticles(Pageable pageable);

    @Query("SELECT a FROM BoardArticle a JOIN FETCH a.board WHERE a.board.site.code = :siteCode AND a.deleted = false ORDER BY a.createdAt DESC")
    List<BoardArticle> findRecentArticlesBySite(@Param("siteCode") String siteCode, Pageable pageable);
}
