package com.ysk.cms.domain.user.menu.service;

import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.admin.site.entity.Site;
import com.ysk.cms.domain.admin.site.repository.SiteRepository;
import com.ysk.cms.domain.admin.user.entity.Role;
import com.ysk.cms.domain.admin.user.repository.RoleRepository;
import com.ysk.cms.domain.user.menu.dto.*;
import com.ysk.cms.domain.user.menu.entity.UserMenu;
import com.ysk.cms.domain.user.menu.entity.UserMenuStatus;
import com.ysk.cms.domain.user.menu.repository.UserMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMenuService {

    private final UserMenuRepository userMenuRepository;
    private final SiteRepository siteRepository;
    private final RoleRepository roleRepository;

    public List<UserMenuDto> getMenus(String siteCode) {
        return userMenuRepository.findBySiteCode(siteCode).stream()
                .map(UserMenuDto::from)
                .toList();
    }

    public List<UserMenuTreeDto> getMenuTree(String siteCode) {
        List<UserMenu> rootMenus = userMenuRepository.findRootMenusBySiteCode(siteCode);
        return rootMenus.stream()
                .map(UserMenuTreeDto::fromWithChildren)
                .toList();
    }

    public List<UserMenuTreeDto> getActiveMenuTree(String siteCode) {
        List<UserMenu> rootMenus = userMenuRepository.findRootMenusBySiteCodeAndStatus(siteCode, UserMenuStatus.ACTIVE);
        return rootMenus.stream()
                .map(UserMenuTreeDto::fromWithChildren)
                .toList();
    }

    public UserMenuDto getMenu(String siteCode, Long menuId) {
        UserMenu menu = userMenuRepository.findByIdAndSiteCode(menuId, siteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_MENU_NOT_FOUND));
        return UserMenuDto.from(menu);
    }

    public List<UserMenuDto> getChildMenus(Long parentId) {
        return userMenuRepository.findByParentId(parentId).stream()
                .map(UserMenuDto::from)
                .toList();
    }

    @Transactional
    public UserMenuDto createMenu(String siteCode, UserMenuCreateRequest request) {
        Site site = siteRepository.findByCode(siteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.SITE_NOT_FOUND));

        // 코드 중복 확인
        if (request.getCode() != null && !request.getCode().isBlank()) {
            if (userMenuRepository.existsBySiteCodeAndCode(siteCode, request.getCode())) {
                throw new BusinessException(ErrorCode.DUPLICATE_USER_MENU_CODE);
            }
        }

        UserMenu parent = null;
        Integer sortOrder = request.getSortOrder();

        if (request.getParentId() != null) {
            parent = userMenuRepository.findByIdAndSiteCode(request.getParentId(), siteCode)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_MENU_NOT_FOUND));

            if (sortOrder == null) {
                sortOrder = userMenuRepository.findMaxSortOrderByParentId(request.getParentId()) + 1;
            }
        } else {
            if (sortOrder == null) {
                sortOrder = userMenuRepository.findMaxSortOrderByRootMenus(siteCode) + 1;
            }
        }

        // 권한 조회
        Set<Role> roles = new HashSet<>();
        if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
            roles = new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
        }

        UserMenu menu = UserMenu.builder()
                .site(site)
                .parent(parent)
                .name(request.getName())
                .code(request.getCode())
                .type(request.getType())
                .url(request.getUrl())
                .icon(request.getIcon())
                .sortOrder(sortOrder)
                .status(request.getStatus())
                .target(request.getTarget())
                .description(request.getDescription())
                .roles(roles)
                .build();

        UserMenu savedMenu = userMenuRepository.save(menu);
        return UserMenuDto.from(savedMenu);
    }

    @Transactional
    public UserMenuDto updateMenu(String siteCode, Long menuId, UserMenuUpdateRequest request) {
        UserMenu menu = userMenuRepository.findByIdAndSiteCode(menuId, siteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_MENU_NOT_FOUND));

        // 코드 중복 확인 (다른 메뉴가 이미 사용 중인지)
        if (request.getCode() != null && !request.getCode().isBlank()) {
            userMenuRepository.findBySiteCodeAndCode(siteCode, request.getCode())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(menuId)) {
                            throw new BusinessException(ErrorCode.DUPLICATE_USER_MENU_CODE);
                        }
                    });
        }

        // 부모 변경 처리
        if (request.getParentId() != null) {
            if (request.getParentId().equals(menuId)) {
                throw new BusinessException(ErrorCode.INVALID_USER_MENU_PARENT);
            }
            UserMenu newParent = userMenuRepository.findByIdAndSiteCode(request.getParentId(), siteCode)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_MENU_NOT_FOUND));

            // 순환 참조 체크
            if (isDescendant(menu, newParent)) {
                throw new BusinessException(ErrorCode.CIRCULAR_USER_MENU_REFERENCE);
            }
            menu.updateParent(newParent);
        } else if (menu.getParent() != null && request.getParentId() == null) {
            // 루트로 이동
            menu.updateParent(null);
        }

        menu.update(
                request.getName(),
                request.getCode() != null ? request.getCode() : menu.getCode(),
                request.getType() != null ? request.getType() : menu.getType(),
                request.getUrl() != null ? request.getUrl() : menu.getUrl(),
                request.getIcon() != null ? request.getIcon() : menu.getIcon(),
                request.getSortOrder() != null ? request.getSortOrder() : menu.getSortOrder(),
                request.getStatus() != null ? request.getStatus() : menu.getStatus(),
                request.getTarget() != null ? request.getTarget() : menu.getTarget(),
                request.getDescription() != null ? request.getDescription() : menu.getDescription()
        );

        // 권한 업데이트
        if (request.getRoleIds() != null) {
            Set<Role> roles = request.getRoleIds().isEmpty()
                    ? new HashSet<>()
                    : new HashSet<>(roleRepository.findAllById(request.getRoleIds()));
            menu.updateRoles(roles);
        }

        return UserMenuDto.from(menu);
    }

    @Transactional
    public void deleteMenu(String siteCode, Long menuId) {
        UserMenu menu = userMenuRepository.findByIdAndSiteCodeWithChildren(menuId, siteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_MENU_NOT_FOUND));

        // 하위 메뉴도 함께 삭제 (soft delete)
        deleteMenuRecursively(menu);
    }

    private void deleteMenuRecursively(UserMenu menu) {
        if (menu.getChildren() != null) {
            for (UserMenu child : menu.getChildren()) {
                deleteMenuRecursively(child);
            }
        }
        menu.delete();
    }

    @Transactional
    public void sortMenus(String siteCode, UserMenuSortRequest request) {
        for (UserMenuSortRequest.UserMenuSortItem item : request.getItems()) {
            UserMenu menu = userMenuRepository.findByIdAndSiteCode(item.getId(), siteCode)
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_MENU_NOT_FOUND));

            // 부모 변경
            if (item.getParentId() != null) {
                if (!item.getParentId().equals(menu.getParent() != null ? menu.getParent().getId() : null)) {
                    UserMenu newParent = userMenuRepository.findByIdAndSiteCode(item.getParentId(), siteCode)
                            .orElseThrow(() -> new BusinessException(ErrorCode.USER_MENU_NOT_FOUND));
                    menu.updateParent(newParent);
                }
            } else {
                menu.updateParent(null);
            }

            menu.updateSortOrder(item.getSortOrder());
        }
    }

    private boolean isDescendant(UserMenu ancestor, UserMenu descendant) {
        if (descendant == null) {
            return false;
        }
        UserMenu current = descendant.getParent();
        while (current != null) {
            if (current.getId().equals(ancestor.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
}
