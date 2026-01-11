package com.ysk.cms.domain.board.repository;

import com.ysk.cms.domain.board.entity.BoardTypeEntity;
import com.ysk.cms.domain.board.entity.BoardTypeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardTypeRepository extends JpaRepository<BoardTypeEntity, Long> {

    @Query("SELECT bt FROM BoardTypeEntity bt WHERE bt.site.code = :siteCode AND bt.deleted = false ORDER BY bt.sortOrder")
    List<BoardTypeEntity> findBySiteCode(@Param("siteCode") String siteCode);

    @Query("SELECT bt FROM BoardTypeEntity bt WHERE bt.site.code = :siteCode AND bt.deleted = false")
    Page<BoardTypeEntity> findBySiteCode(@Param("siteCode") String siteCode, Pageable pageable);

    @Query("SELECT bt FROM BoardTypeEntity bt WHERE bt.site.code = :siteCode AND bt.code = :code AND bt.deleted = false")
    Optional<BoardTypeEntity> findBySiteCodeAndCode(@Param("siteCode") String siteCode, @Param("code") String code);

    @Query("SELECT bt FROM BoardTypeEntity bt WHERE bt.site.code = :siteCode AND bt.status = :status AND bt.deleted = false ORDER BY bt.sortOrder")
    List<BoardTypeEntity> findBySiteCodeAndStatus(@Param("siteCode") String siteCode, @Param("status") BoardTypeStatus status);

    @Query("SELECT COUNT(bt) > 0 FROM BoardTypeEntity bt WHERE bt.site.code = :siteCode AND bt.code = :code AND bt.deleted = false")
    boolean existsBySiteCodeAndCode(@Param("siteCode") String siteCode, @Param("code") String code);
}
