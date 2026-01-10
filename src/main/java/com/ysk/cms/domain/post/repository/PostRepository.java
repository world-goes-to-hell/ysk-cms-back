package com.ysk.cms.domain.post.repository;

import com.ysk.cms.domain.post.entity.Post;
import com.ysk.cms.domain.post.entity.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p WHERE p.board.id = :boardId AND p.deleted = false")
    Page<Post> findByBoardId(@Param("boardId") Long boardId, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.board.site.code = :siteCode AND p.board.code = :boardCode AND p.deleted = false")
    Page<Post> findBySiteCodeAndBoardCode(@Param("siteCode") String siteCode, @Param("boardCode") String boardCode, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.board.site.code = :siteCode AND p.board.code = :boardCode AND p.status = :status AND p.deleted = false")
    Page<Post> findBySiteCodeAndBoardCodeAndStatus(
            @Param("siteCode") String siteCode,
            @Param("boardCode") String boardCode,
            @Param("status") PostStatus status,
            Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.board.site.code = :siteCode AND p.board.code = :boardCode AND p.deleted = false")
    Optional<Post> findByIdAndSiteCodeAndBoardCode(
            @Param("id") Long id,
            @Param("siteCode") String siteCode,
            @Param("boardCode") String boardCode);

    @Query("SELECT p FROM Post p WHERE p.board.site.code = :siteCode AND p.board.code = :boardCode AND p.isPinned = true AND p.status = 'PUBLISHED' AND p.deleted = false ORDER BY p.createdAt DESC")
    Page<Post> findPinnedPosts(@Param("siteCode") String siteCode, @Param("boardCode") String boardCode, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.board.id = :boardId AND p.deleted = false")
    long countByBoardId(@Param("boardId") Long boardId);

    @Query("""
            SELECT p FROM Post p
            WHERE p.board.site.code = :siteCode
            AND p.board.code = :boardCode
            AND p.deleted = false
            AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)
            """)
    Page<Post> searchByKeyword(
            @Param("siteCode") String siteCode,
            @Param("boardCode") String boardCode,
            @Param("keyword") String keyword,
            Pageable pageable);

    // Dashboard queries
    @Query("SELECT COUNT(p) FROM Post p WHERE p.deleted = false")
    long countAllActive();

    @Query("SELECT COUNT(p) FROM Post p WHERE p.deleted = false AND p.createdAt >= :startDate")
    long countPostsCreatedSince(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.deleted = false AND p.createdAt >= :startDate AND p.createdAt < :endDate")
    long countPostsCreatedBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Post p JOIN FETCH p.board WHERE p.deleted = false ORDER BY p.createdAt DESC")
    List<Post> findRecentPosts(Pageable pageable);

    @Query("SELECT p FROM Post p JOIN FETCH p.board WHERE p.board.site.code = :siteCode AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Post> findRecentPostsBySite(@Param("siteCode") String siteCode, Pageable pageable);
}
