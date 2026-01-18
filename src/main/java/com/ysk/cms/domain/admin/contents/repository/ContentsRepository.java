package com.ysk.cms.domain.admin.contents.repository;

import com.ysk.cms.domain.admin.contents.entity.Contents;
import com.ysk.cms.domain.admin.contents.entity.ContentsStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContentsRepository extends JpaRepository<Contents, Long> {

    @Query("SELECT c FROM Contents c WHERE c.site.code = :siteCode AND c.deleted = false ORDER BY c.sortOrder")
    List<Contents> findBySiteCode(@Param("siteCode") String siteCode);

    @Query("SELECT c FROM Contents c WHERE c.site.code = :siteCode AND c.deleted = false")
    Page<Contents> findBySiteCode(@Param("siteCode") String siteCode, Pageable pageable);

    @Query("SELECT c FROM Contents c WHERE c.site.code = :siteCode AND c.slug = :slug AND c.deleted = false")
    Optional<Contents> findBySiteCodeAndSlug(@Param("siteCode") String siteCode, @Param("slug") String slug);

    @Query("SELECT c FROM Contents c WHERE c.site.code = :siteCode AND c.id = :id AND c.deleted = false")
    Optional<Contents> findBySiteCodeAndId(@Param("siteCode") String siteCode, @Param("id") Long id);

    @Query("SELECT c FROM Contents c WHERE c.site.code = :siteCode AND c.status = :status AND c.deleted = false ORDER BY c.sortOrder")
    List<Contents> findBySiteCodeAndStatus(@Param("siteCode") String siteCode, @Param("status") ContentsStatus status);

    @Query("SELECT c FROM Contents c WHERE c.site.code = :siteCode AND c.parent IS NULL AND c.deleted = false ORDER BY c.sortOrder")
    List<Contents> findRootContentsBySiteCode(@Param("siteCode") String siteCode);

    @Query("SELECT c FROM Contents c WHERE c.parent.id = :parentId AND c.deleted = false ORDER BY c.sortOrder")
    List<Contents> findByParentId(@Param("parentId") Long parentId);

    @Query("SELECT COUNT(c) > 0 FROM Contents c WHERE c.site.code = :siteCode AND c.slug = :slug AND c.deleted = false")
    boolean existsBySiteCodeAndSlug(@Param("siteCode") String siteCode, @Param("slug") String slug);

    @Query("SELECT COUNT(c) > 0 FROM Contents c WHERE c.site.code = :siteCode AND c.slug = :slug AND c.id != :excludeId AND c.deleted = false")
    boolean existsBySiteCodeAndSlugExcludingId(@Param("siteCode") String siteCode, @Param("slug") String slug, @Param("excludeId") Long excludeId);
}
