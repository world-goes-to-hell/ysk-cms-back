package com.ysk.cms.domain.page.service;

import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.page.dto.*;
import com.ysk.cms.domain.page.entity.Page;
import com.ysk.cms.domain.page.entity.PageStatus;
import com.ysk.cms.domain.page.repository.PageRepository;
import com.ysk.cms.domain.site.entity.Site;
import com.ysk.cms.domain.site.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PageService {

    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;

    public List<PageListDto> getPagesBySite(String siteCode) {
        return pageRepository.findBySiteCode(siteCode).stream()
                .map(PageListDto::from)
                .toList();
    }

    public PageResponse<PageListDto> getPagesBySite(String siteCode, Pageable pageable) {
        org.springframework.data.domain.Page<Page> pagePage = pageRepository.findBySiteCode(siteCode, pageable);
        return PageResponse.of(pagePage.map(PageListDto::from));
    }

    public List<PageDto> getRootPages(String siteCode) {
        return pageRepository.findRootPagesBySiteCode(siteCode).stream()
                .map(PageDto::fromWithChildren)
                .toList();
    }

    public List<PageListDto> getPublishedPages(String siteCode) {
        return pageRepository.findBySiteCodeAndStatus(siteCode, PageStatus.PUBLISHED).stream()
                .map(PageListDto::from)
                .toList();
    }

    public PageDto getPage(String siteCode, Long pageId) {
        Page page = pageRepository.findBySiteCodeAndId(siteCode, pageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAGE_NOT_FOUND));
        return PageDto.from(page);
    }

    public PageDto getPageBySlug(String siteCode, String slug) {
        Page page = pageRepository.findBySiteCodeAndSlug(siteCode, slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAGE_NOT_FOUND));
        return PageDto.from(page);
    }

    public List<PageListDto> getChildPages(String siteCode, Long parentId) {
        pageRepository.findBySiteCodeAndId(siteCode, parentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAGE_NOT_FOUND));

        return pageRepository.findByParentId(parentId).stream()
                .map(PageListDto::from)
                .toList();
    }

    @Transactional
    public PageDto createPage(String siteCode, PageCreateRequest request) {
        Site site = siteRepository.findByCode(siteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.SITE_NOT_FOUND));

        if (pageRepository.existsBySiteCodeAndSlug(siteCode, request.getSlug())) {
            throw new BusinessException(ErrorCode.DUPLICATE_PAGE_SLUG);
        }

        Page parent = null;
        if (request.getParentId() != null) {
            parent = pageRepository.findBySiteCodeAndId(siteCode, request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PAGE_NOT_FOUND));
        }

        Page page = Page.builder()
                .site(site)
                .slug(request.getSlug())
                .title(request.getTitle())
                .content(request.getContent())
                .metaDescription(request.getMetaDescription())
                .metaKeywords(request.getMetaKeywords())
                .status(request.getStatus())
                .parent(parent)
                .sortOrder(request.getSortOrder())
                .build();

        if (request.getStatus() == PageStatus.PUBLISHED) {
            page.publish();
        }

        Page savedPage = pageRepository.save(page);
        return PageDto.from(savedPage);
    }

    @Transactional
    public PageDto updatePage(String siteCode, Long pageId, PageUpdateRequest request) {
        Page page = pageRepository.findBySiteCodeAndId(siteCode, pageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAGE_NOT_FOUND));

        if (!page.getSlug().equals(request.getSlug()) &&
                pageRepository.existsBySiteCodeAndSlugExcludingId(siteCode, request.getSlug(), pageId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_PAGE_SLUG);
        }

        Page parent = null;
        if (request.getParentId() != null) {
            if (request.getParentId().equals(pageId)) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            parent = pageRepository.findBySiteCodeAndId(siteCode, request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PAGE_NOT_FOUND));
        }

        page.update(
                request.getTitle(),
                request.getContent(),
                request.getSlug(),
                request.getMetaDescription(),
                request.getMetaKeywords(),
                request.getStatus(),
                request.getSortOrder()
        );
        page.updateParent(parent);

        return PageDto.from(page);
    }

    @Transactional
    public void deletePage(String siteCode, Long pageId) {
        Page page = pageRepository.findBySiteCodeAndId(siteCode, pageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAGE_NOT_FOUND));

        page.delete();
    }

    @Transactional
    public PageDto publishPage(String siteCode, Long pageId) {
        Page page = pageRepository.findBySiteCodeAndId(siteCode, pageId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAGE_NOT_FOUND));

        page.publish();
        return PageDto.from(page);
    }
}
