package com.ysk.cms.domain.admin.site.repository;

import com.ysk.cms.domain.admin.site.entity.Site;
import com.ysk.cms.domain.admin.site.entity.SiteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SiteRepository extends JpaRepository<Site, Long> {

    Optional<Site> findByCode(String code);

    boolean existsByCode(String code);

    @Query("SELECT s FROM Site s WHERE s.deleted = false")
    Page<Site> findAllActive(Pageable pageable);

    @Query("SELECT s FROM Site s WHERE s.deleted = false")
    List<Site> findAllActive();

    @Query("SELECT s FROM Site s WHERE s.deleted = false AND s.status = :status")
    List<Site> findAllByStatus(@Param("status") SiteStatus status);

    @Query("SELECT s FROM Site s WHERE s.code = :code AND s.deleted = false")
    Optional<Site> findByCodeAndNotDeleted(@Param("code") String code);
}
