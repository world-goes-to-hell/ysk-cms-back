package com.ysk.cms.domain.admin.menu.repository;

import com.ysk.cms.domain.admin.menu.entity.Menu;
import com.ysk.cms.domain.admin.menu.entity.MenuStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query("SELECT m FROM Menu m WHERE m.site.code = :siteCode AND m.deleted = false ORDER BY m.sortOrder ASC")
    List<Menu> findBySiteCode(@Param("siteCode") String siteCode);

    @Query("SELECT m FROM Menu m WHERE m.site.code = :siteCode AND m.parent IS NULL AND m.deleted = false ORDER BY m.sortOrder ASC")
    List<Menu> findRootMenusBySiteCode(@Param("siteCode") String siteCode);

    @Query("SELECT m FROM Menu m WHERE m.site.code = :siteCode AND m.parent IS NULL AND m.status = :status AND m.deleted = false ORDER BY m.sortOrder ASC")
    List<Menu> findRootMenusBySiteCodeAndStatus(@Param("siteCode") String siteCode, @Param("status") MenuStatus status);

    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.children WHERE m.site.code = :siteCode AND m.parent IS NULL AND m.deleted = false ORDER BY m.sortOrder ASC")
    List<Menu> findRootMenusWithChildrenBySiteCode(@Param("siteCode") String siteCode);

    @Query("SELECT m FROM Menu m WHERE m.parent.id = :parentId AND m.deleted = false ORDER BY m.sortOrder ASC")
    List<Menu> findByParentId(@Param("parentId") Long parentId);

    @Query("SELECT m FROM Menu m WHERE m.id = :id AND m.site.code = :siteCode AND m.deleted = false")
    Optional<Menu> findByIdAndSiteCode(@Param("id") Long id, @Param("siteCode") String siteCode);

    @Query("SELECT m FROM Menu m LEFT JOIN FETCH m.children WHERE m.id = :id AND m.site.code = :siteCode AND m.deleted = false")
    Optional<Menu> findByIdAndSiteCodeWithChildren(@Param("id") Long id, @Param("siteCode") String siteCode);

    @Query("SELECT m FROM Menu m WHERE m.site.code = :siteCode AND m.code = :code AND m.deleted = false")
    Optional<Menu> findBySiteCodeAndCode(@Param("siteCode") String siteCode, @Param("code") String code);

    @Query("SELECT COALESCE(MAX(m.sortOrder), 0) FROM Menu m WHERE m.site.code = :siteCode AND m.parent IS NULL AND m.deleted = false")
    Integer findMaxSortOrderByRootMenus(@Param("siteCode") String siteCode);

    @Query("SELECT COALESCE(MAX(m.sortOrder), 0) FROM Menu m WHERE m.parent.id = :parentId AND m.deleted = false")
    Integer findMaxSortOrderByParentId(@Param("parentId") Long parentId);

    boolean existsBySiteCodeAndCode(String siteCode, String code);

    // roles가 설정된 모든 활성 메뉴 조회 (동적 권한 체크용)
    @Query("SELECT m FROM Menu m WHERE m.roles IS NOT NULL AND m.roles <> '' AND m.status = 'ACTIVE' AND m.deleted = false")
    List<Menu> findAllWithRoles();
}
