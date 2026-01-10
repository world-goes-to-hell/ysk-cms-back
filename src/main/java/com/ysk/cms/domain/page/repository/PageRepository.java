package com.ysk.cms.domain.page.repository;

import com.ysk.cms.domain.page.entity.Page;
import com.ysk.cms.domain.page.entity.PageStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PageRepository extends JpaRepository<Page, Long> {

    @Query("SELECT p FROM Page p WHERE p.site.code = :siteCode AND p.deleted = false ORDER BY p.sortOrder")
    List<Page> findBySiteCode(@Param("siteCode") String siteCode);

    @Query("SELECT p FROM Page p WHERE p.site.code = :siteCode AND p.deleted = false")
    org.springframework.data.domain.Page<Page> findBySiteCode(@Param("siteCode") String siteCode, Pageable pageable);

    @Query("SELECT p FROM Page p WHERE p.site.code = :siteCode AND p.slug = :slug AND p.deleted = false")
    Optional<Page> findBySiteCodeAndSlug(@Param("siteCode") String siteCode, @Param("slug") String slug);

    @Query("SELECT p FROM Page p WHERE p.site.code = :siteCode AND p.id = :id AND p.deleted = false")
    Optional<Page> findBySiteCodeAndId(@Param("siteCode") String siteCode, @Param("id") Long id);

    @Query("SELECT p FROM Page p WHERE p.site.code = :siteCode AND p.status = :status AND p.deleted = false ORDER BY p.sortOrder")
    List<Page> findBySiteCodeAndStatus(@Param("siteCode") String siteCode, @Param("status") PageStatus status);

    @Query("SELECT p FROM Page p WHERE p.site.code = :siteCode AND p.parent IS NULL AND p.deleted = false ORDER BY p.sortOrder")
    List<Page> findRootPagesBySiteCode(@Param("siteCode") String siteCode);

    @Query("SELECT p FROM Page p WHERE p.parent.id = :parentId AND p.deleted = false ORDER BY p.sortOrder")
    List<Page> findByParentId(@Param("parentId") Long parentId);

    @Query("SELECT COUNT(p) > 0 FROM Page p WHERE p.site.code = :siteCode AND p.slug = :slug AND p.deleted = false")
    boolean existsBySiteCodeAndSlug(@Param("siteCode") String siteCode, @Param("slug") String slug);

    @Query("SELECT COUNT(p) > 0 FROM Page p WHERE p.site.code = :siteCode AND p.slug = :slug AND p.id != :excludeId AND p.deleted = false")
    boolean existsBySiteCodeAndSlugExcludingId(@Param("siteCode") String siteCode, @Param("slug") String slug, @Param("excludeId") Long excludeId);
}
