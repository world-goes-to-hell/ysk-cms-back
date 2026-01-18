package com.ysk.cms.domain.admin.board.repository;

import com.ysk.cms.domain.admin.board.entity.Board;
import com.ysk.cms.domain.admin.board.entity.BoardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    @Query("SELECT b FROM Board b WHERE b.site.code = :siteCode AND b.deleted = false ORDER BY b.sortOrder")
    List<Board> findBySiteCode(@Param("siteCode") String siteCode);

    @Query("SELECT b FROM Board b WHERE b.site.code = :siteCode AND b.deleted = false")
    Page<Board> findBySiteCode(@Param("siteCode") String siteCode, Pageable pageable);

    @Query("SELECT b FROM Board b WHERE b.site.code = :siteCode AND b.code = :code AND b.deleted = false")
    Optional<Board> findBySiteCodeAndCode(@Param("siteCode") String siteCode, @Param("code") String code);

    @Query("SELECT b FROM Board b WHERE b.site.code = :siteCode AND b.status = :status AND b.deleted = false ORDER BY b.sortOrder")
    List<Board> findBySiteCodeAndStatus(@Param("siteCode") String siteCode, @Param("status") BoardStatus status);

    boolean existsBySiteCodeAndCode(String siteCode, String code);

    @Query("SELECT COUNT(b) > 0 FROM Board b WHERE b.site.code = :siteCode AND b.code = :code AND b.id != :excludeId AND b.deleted = false")
    boolean existsBySiteCodeAndCodeExcludingId(@Param("siteCode") String siteCode, @Param("code") String code, @Param("excludeId") Long excludeId);
}
