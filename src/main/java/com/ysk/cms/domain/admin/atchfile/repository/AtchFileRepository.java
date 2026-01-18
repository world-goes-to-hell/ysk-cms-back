package com.ysk.cms.domain.admin.atchfile.repository;

import com.ysk.cms.domain.admin.atchfile.entity.AtchFile;
import com.ysk.cms.domain.admin.atchfile.entity.AtchFileType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AtchFileRepository extends JpaRepository<AtchFile, Long> {

    @Query("SELECT f FROM AtchFile f WHERE f.article.id = :articleId AND f.deleted = false")
    List<AtchFile> findByArticleId(@Param("articleId") Long articleId);

    @Query("SELECT f FROM AtchFile f WHERE f.deleted = false")
    Page<AtchFile> findAllActive(Pageable pageable);

    @Query("SELECT f FROM AtchFile f WHERE f.site.code = :siteCode AND f.deleted = false")
    Page<AtchFile> findBySiteCode(@Param("siteCode") String siteCode, Pageable pageable);

    @Query("SELECT f FROM AtchFile f WHERE f.site.code = :siteCode AND f.type = :type AND f.deleted = false")
    Page<AtchFile> findBySiteCodeAndType(@Param("siteCode") String siteCode, @Param("type") AtchFileType type, Pageable pageable);

    @Query("SELECT f FROM AtchFile f WHERE f.type = :type AND f.deleted = false")
    Page<AtchFile> findByType(@Param("type") AtchFileType type, Pageable pageable);

    @Query("SELECT f FROM AtchFile f WHERE f.id = :id AND f.deleted = false")
    Optional<AtchFile> findByIdAndNotDeleted(@Param("id") Long id);

    @Query("SELECT f FROM AtchFile f WHERE f.site.code = :siteCode AND f.id = :id AND f.deleted = false")
    Optional<AtchFile> findBySiteCodeAndId(@Param("siteCode") String siteCode, @Param("id") Long id);

    @Query("SELECT f FROM AtchFile f WHERE f.storedName = :storedName AND f.deleted = false")
    Optional<AtchFile> findByStoredName(@Param("storedName") String storedName);

    @Query("""
            SELECT f FROM AtchFile f
            WHERE f.site.code = :siteCode
            AND f.deleted = false
            AND (f.originalName LIKE %:keyword% OR f.description LIKE %:keyword%)
            """)
    Page<AtchFile> searchByKeyword(@Param("siteCode") String siteCode, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT SUM(f.fileSize) FROM AtchFile f WHERE f.site.code = :siteCode AND f.deleted = false")
    Long getTotalFileSizeBySite(@Param("siteCode") String siteCode);

    @Query("SELECT COUNT(f) FROM AtchFile f WHERE f.site.code = :siteCode AND f.deleted = false")
    long countBySiteCode(@Param("siteCode") String siteCode);
}
