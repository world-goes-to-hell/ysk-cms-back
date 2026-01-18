package com.ysk.cms.domain.admin.menu.service;

import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.admin.menu.dto.*;
import com.ysk.cms.domain.admin.menu.dto.*;
import com.ysk.cms.domain.admin.menu.entity.Menu;
import com.ysk.cms.domain.admin.menu.entity.MenuStatus;
import com.ysk.cms.domain.admin.menu.repository.MenuRepository;
import com.ysk.cms.domain.admin.site.entity.Site;
import com.ysk.cms.domain.admin.site.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final SiteRepository siteRepository;

    public List<MenuDto> getMenus(String siteCode) {
        return menuRepository.findBySiteCode(siteCode).stream()
                .map(MenuDto::from)
                .toList();
    }

    public List<MenuTreeDto> getMenuTree(String siteCode) {
        List<Menu> rootMenus = menuRepository.findRootMenusBySiteCode(siteCode);
        return rootMenus.stream()
                .map(MenuTreeDto::fromWithChildren)
                .toList();
    }

    public List<MenuTreeDto> getActiveMenuTree(String siteCode) {
        List<Menu> rootMenus = menuRepository.findRootMenusBySiteCodeAndStatus(siteCode, MenuStatus.ACTIVE);
        return rootMenus.stream()
                .map(MenuTreeDto::fromWithChildren)
                .toList();
    }

    public MenuDto getMenu(String siteCode, Long menuId) {
        Menu menu = menuRepository.findByIdAndSiteCode(menuId, siteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        return MenuDto.from(menu);
    }

    public List<MenuDto> getChildMenus(Long parentId) {
        return menuRepository.findByParentId(parentId).stream()
                .map(MenuDto::from)
                .toList();
    }

    @Transactional
    public MenuDto createMenu(String siteCode, MenuCreateRequest request) {
        Site site = siteRepository.findByCode(siteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.SITE_NOT_FOUND));

        // 코드 중복 확인
        if (request.getCode() != null && !request.getCode().isBlank()) {
            if (menuRepository.existsBySiteCodeAndCode(siteCode, request.getCode())) {
                throw new BusinessException(ErrorCode.DUPLICATE_MENU_CODE);
            }
        }

        Menu parent = null;
        Integer sortOrder = request.getSortOrder();

        if (request.getParentId() != null) {
            parent = menuRepository.findByIdAndSiteCode(request.getParentId(), siteCode)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

            if (sortOrder == null) {
                sortOrder = menuRepository.findMaxSortOrderByParentId(request.getParentId()) + 1;
            }
        } else {
            if (sortOrder == null) {
                sortOrder = menuRepository.findMaxSortOrderByRootMenus(siteCode) + 1;
            }
        }

        Menu menu = Menu.builder()
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
                .roles(request.getRoles())
                .description(request.getDescription())
                .componentPath(request.getComponentPath())
                .relatedRoutes(request.getRelatedRoutes())
                .build();

        Menu savedMenu = menuRepository.save(menu);
        return MenuDto.from(savedMenu);
    }

    @Transactional
    public MenuDto updateMenu(String siteCode, Long menuId, MenuUpdateRequest request) {
        Menu menu = menuRepository.findByIdAndSiteCode(menuId, siteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        // 코드 중복 확인 (다른 메뉴가 이미 사용 중인지)
        if (request.getCode() != null && !request.getCode().isBlank()) {
            menuRepository.findBySiteCodeAndCode(siteCode, request.getCode())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(menuId)) {
                            throw new BusinessException(ErrorCode.DUPLICATE_MENU_CODE);
                        }
                    });
        }

        // 부모 변경 처리
        if (request.getParentId() != null) {
            if (request.getParentId().equals(menuId)) {
                throw new BusinessException(ErrorCode.INVALID_MENU_PARENT);
            }
            Menu newParent = menuRepository.findByIdAndSiteCode(request.getParentId(), siteCode)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

            // 순환 참조 체크
            if (isDescendant(menu, newParent)) {
                throw new BusinessException(ErrorCode.CIRCULAR_MENU_REFERENCE);
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
                request.getRoles() != null ? request.getRoles() : menu.getRoles(),
                request.getDescription() != null ? request.getDescription() : menu.getDescription(),
                request.getComponentPath() != null ? request.getComponentPath() : menu.getComponentPath(),
                request.getRelatedRoutes() != null ? request.getRelatedRoutes() : menu.getRelatedRoutes()
        );

        return MenuDto.from(menu);
    }

    @Transactional
    public void deleteMenu(String siteCode, Long menuId) {
        Menu menu = menuRepository.findByIdAndSiteCodeWithChildren(menuId, siteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        // 하위 메뉴도 함께 삭제 (soft delete)
        deleteMenuRecursively(menu);
    }

    private void deleteMenuRecursively(Menu menu) {
        if (menu.getChildren() != null) {
            for (Menu child : menu.getChildren()) {
                deleteMenuRecursively(child);
            }
        }
        menu.delete();
    }

    @Transactional
    public void sortMenus(String siteCode, MenuSortRequest request) {
        for (MenuSortRequest.MenuSortItem item : request.getItems()) {
            Menu menu = menuRepository.findByIdAndSiteCode(item.getId(), siteCode)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

            // 부모 변경
            if (item.getParentId() != null) {
                if (!item.getParentId().equals(menu.getParent() != null ? menu.getParent().getId() : null)) {
                    Menu newParent = menuRepository.findByIdAndSiteCode(item.getParentId(), siteCode)
                            .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
                    menu.updateParent(newParent);
                }
            } else {
                menu.updateParent(null);
            }

            menu.updateSortOrder(item.getSortOrder());
        }
    }

    private boolean isDescendant(Menu ancestor, Menu descendant) {
        if (descendant == null) {
            return false;
        }
        Menu current = descendant.getParent();
        while (current != null) {
            if (current.getId().equals(ancestor.getId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
}
