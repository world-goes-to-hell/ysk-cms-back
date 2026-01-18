package com.ysk.cms.domain.admin.contents.service;

import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.admin.contents.dto.ContentsCreateRequest;
import com.ysk.cms.domain.admin.contents.dto.ContentsDto;
import com.ysk.cms.domain.admin.contents.dto.ContentsListDto;
import com.ysk.cms.domain.admin.contents.dto.ContentsUpdateRequest;
import com.ysk.cms.domain.admin.contents.dto.*;
import com.ysk.cms.domain.admin.contents.entity.Contents;
import com.ysk.cms.domain.admin.contents.entity.ContentsStatus;
import com.ysk.cms.domain.admin.contents.repository.ContentsRepository;
import com.ysk.cms.domain.admin.site.entity.Site;
import com.ysk.cms.domain.admin.site.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentsService {

    private final ContentsRepository contentsRepository;
    private final SiteRepository siteRepository;

    public List<ContentsListDto> getContentsBySite(String siteCode) {
        return contentsRepository.findBySiteCode(siteCode).stream()
                .map(ContentsListDto::from)
                .toList();
    }

    public PageResponse<ContentsListDto> getContentsBySite(String siteCode, Pageable pageable) {
        Page<Contents> contentsPage = contentsRepository.findBySiteCode(siteCode, pageable);
        return PageResponse.of(contentsPage.map(ContentsListDto::from));
    }

    public List<ContentsDto> getRootContents(String siteCode) {
        return contentsRepository.findRootContentsBySiteCode(siteCode).stream()
                .map(ContentsDto::fromWithChildren)
                .toList();
    }

    public List<ContentsListDto> getPublishedContents(String siteCode) {
        return contentsRepository.findBySiteCodeAndStatus(siteCode, ContentsStatus.PUBLISHED).stream()
                .map(ContentsListDto::from)
                .toList();
    }

    public ContentsDto getContents(String siteCode, Long contentsId) {
        Contents contents = contentsRepository.findBySiteCodeAndId(siteCode, contentsId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENTS_NOT_FOUND));
        return ContentsDto.from(contents);
    }

    public ContentsDto getContentsBySlug(String siteCode, String slug) {
        Contents contents = contentsRepository.findBySiteCodeAndSlug(siteCode, slug)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENTS_NOT_FOUND));
        return ContentsDto.from(contents);
    }

    public List<ContentsListDto> getChildContents(String siteCode, Long parentId) {
        contentsRepository.findBySiteCodeAndId(siteCode, parentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENTS_NOT_FOUND));

        return contentsRepository.findByParentId(parentId).stream()
                .map(ContentsListDto::from)
                .toList();
    }

    @Transactional
    public ContentsDto createContents(String siteCode, ContentsCreateRequest request) {
        Site site = siteRepository.findByCode(siteCode)
                .orElseThrow(() -> new BusinessException(ErrorCode.SITE_NOT_FOUND));

        if (contentsRepository.existsBySiteCodeAndSlug(siteCode, request.getSlug())) {
            throw new BusinessException(ErrorCode.DUPLICATE_CONTENTS_SLUG);
        }

        Contents parent = null;
        if (request.getParentId() != null) {
            parent = contentsRepository.findBySiteCodeAndId(siteCode, request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CONTENTS_NOT_FOUND));
        }

        Contents contents = Contents.builder()
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

        if (request.getStatus() == ContentsStatus.PUBLISHED) {
            contents.publish();
        }

        Contents savedContents = contentsRepository.save(contents);
        return ContentsDto.from(savedContents);
    }

    @Transactional
    public ContentsDto updateContents(String siteCode, Long contentsId, ContentsUpdateRequest request) {
        Contents contents = contentsRepository.findBySiteCodeAndId(siteCode, contentsId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENTS_NOT_FOUND));

        if (!contents.getSlug().equals(request.getSlug()) &&
                contentsRepository.existsBySiteCodeAndSlugExcludingId(siteCode, request.getSlug(), contentsId)) {
            throw new BusinessException(ErrorCode.DUPLICATE_CONTENTS_SLUG);
        }

        Contents parent = null;
        if (request.getParentId() != null) {
            if (request.getParentId().equals(contentsId)) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
            }
            parent = contentsRepository.findBySiteCodeAndId(siteCode, request.getParentId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.CONTENTS_NOT_FOUND));
        }

        contents.update(
                request.getTitle(),
                request.getContent(),
                request.getSlug(),
                request.getMetaDescription(),
                request.getMetaKeywords(),
                request.getStatus(),
                request.getSortOrder()
        );
        contents.updateParent(parent);

        return ContentsDto.from(contents);
    }

    @Transactional
    public void deleteContents(String siteCode, Long contentsId) {
        Contents contents = contentsRepository.findBySiteCodeAndId(siteCode, contentsId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENTS_NOT_FOUND));

        contents.delete();
    }

    @Transactional
    public ContentsDto publishContents(String siteCode, Long contentsId) {
        Contents contents = contentsRepository.findBySiteCodeAndId(siteCode, contentsId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENTS_NOT_FOUND));

        contents.publish();
        return ContentsDto.from(contents);
    }
}
