package com.ysk.cms.domain.media.repository;

import com.ysk.cms.domain.media.entity.Media;
import com.ysk.cms.domain.media.entity.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MediaRepository extends JpaRepository<Media, Long> {

    @Query("SELECT m FROM Media m WHERE m.deleted = false")
    Page<Media> findAllActive(Pageable pageable);

    @Query("SELECT m FROM Media m WHERE m.site.code = :siteCode AND m.deleted = false")
    Page<Media> findBySiteCode(@Param("siteCode") String siteCode, Pageable pageable);

    @Query("SELECT m FROM Media m WHERE m.site.code = :siteCode AND m.type = :type AND m.deleted = false")
    Page<Media> findBySiteCodeAndType(@Param("siteCode") String siteCode, @Param("type") MediaType type, Pageable pageable);

    @Query("SELECT m FROM Media m WHERE m.type = :type AND m.deleted = false")
    Page<Media> findByType(@Param("type") MediaType type, Pageable pageable);

    @Query("SELECT m FROM Media m WHERE m.id = :id AND m.deleted = false")
    Optional<Media> findByIdAndNotDeleted(@Param("id") Long id);

    @Query("SELECT m FROM Media m WHERE m.site.code = :siteCode AND m.id = :id AND m.deleted = false")
    Optional<Media> findBySiteCodeAndId(@Param("siteCode") String siteCode, @Param("id") Long id);

    @Query("SELECT m FROM Media m WHERE m.storedName = :storedName AND m.deleted = false")
    Optional<Media> findByStoredName(@Param("storedName") String storedName);

    @Query("""
            SELECT m FROM Media m
            WHERE m.site.code = :siteCode
            AND m.deleted = false
            AND (m.originalName LIKE %:keyword% OR m.description LIKE %:keyword%)
            """)
    Page<Media> searchByKeyword(@Param("siteCode") String siteCode, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT SUM(m.fileSize) FROM Media m WHERE m.site.code = :siteCode AND m.deleted = false")
    Long getTotalFileSizeBySite(@Param("siteCode") String siteCode);

    @Query("SELECT COUNT(m) FROM Media m WHERE m.site.code = :siteCode AND m.deleted = false")
    long countBySiteCode(@Param("siteCode") String siteCode);
}
