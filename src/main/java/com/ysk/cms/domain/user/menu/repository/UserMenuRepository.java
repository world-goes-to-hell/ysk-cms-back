package com.ysk.cms.domain.user.menu.repository;

import com.ysk.cms.domain.user.menu.entity.UserMenu;
import com.ysk.cms.domain.user.menu.entity.UserMenuStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserMenuRepository extends JpaRepository<UserMenu, Long> {

    @Query("SELECT m FROM UserMenu m WHERE m.site.code = :siteCode AND m.deleted = false ORDER BY m.sortOrder ASC")
    List<UserMenu> findBySiteCode(@Param("siteCode") String siteCode);

    @Query("SELECT m FROM UserMenu m WHERE m.site.code = :siteCode AND m.parent IS NULL AND m.deleted = false ORDER BY m.sortOrder ASC")
    List<UserMenu> findRootMenusBySiteCode(@Param("siteCode") String siteCode);

    @Query("SELECT m FROM UserMenu m WHERE m.site.code = :siteCode AND m.parent IS NULL AND m.status = :status AND m.deleted = false ORDER BY m.sortOrder ASC")
    List<UserMenu> findRootMenusBySiteCodeAndStatus(@Param("siteCode") String siteCode, @Param("status") UserMenuStatus status);

    @Query("SELECT m FROM UserMenu m LEFT JOIN FETCH m.children WHERE m.site.code = :siteCode AND m.parent IS NULL AND m.deleted = false ORDER BY m.sortOrder ASC")
    List<UserMenu> findRootMenusWithChildrenBySiteCode(@Param("siteCode") String siteCode);

    @Query("SELECT m FROM UserMenu m WHERE m.parent.id = :parentId AND m.deleted = false ORDER BY m.sortOrder ASC")
    List<UserMenu> findByParentId(@Param("parentId") Long parentId);

    @Query("SELECT m FROM UserMenu m WHERE m.id = :id AND m.site.code = :siteCode AND m.deleted = false")
    Optional<UserMenu> findByIdAndSiteCode(@Param("id") Long id, @Param("siteCode") String siteCode);

    @Query("SELECT m FROM UserMenu m LEFT JOIN FETCH m.children WHERE m.id = :id AND m.site.code = :siteCode AND m.deleted = false")
    Optional<UserMenu> findByIdAndSiteCodeWithChildren(@Param("id") Long id, @Param("siteCode") String siteCode);

    @Query("SELECT m FROM UserMenu m WHERE m.site.code = :siteCode AND m.code = :code AND m.deleted = false")
    Optional<UserMenu> findBySiteCodeAndCode(@Param("siteCode") String siteCode, @Param("code") String code);

    @Query("SELECT COALESCE(MAX(m.sortOrder), 0) FROM UserMenu m WHERE m.site.code = :siteCode AND m.parent IS NULL AND m.deleted = false")
    Integer findMaxSortOrderByRootMenus(@Param("siteCode") String siteCode);

    @Query("SELECT COALESCE(MAX(m.sortOrder), 0) FROM UserMenu m WHERE m.parent.id = :parentId AND m.deleted = false")
    Integer findMaxSortOrderByParentId(@Param("parentId") Long parentId);

    boolean existsBySiteCodeAndCode(String siteCode, String code);
}
