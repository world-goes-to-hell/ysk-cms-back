package com.ysk.cms.domain.site.service;

import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.site.dto.SiteCreateRequest;
import com.ysk.cms.domain.site.dto.SiteDto;
import com.ysk.cms.domain.site.dto.SiteUpdateRequest;
import com.ysk.cms.domain.site.entity.Site;
import com.ysk.cms.domain.site.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteService {

    private final SiteRepository siteRepository;

    public Page<SiteDto> findAll(Pageable pageable) {
        return siteRepository.findAllActive(pageable)
                .map(SiteDto::from);
    }

    public List<SiteDto> findAllList() {
        return siteRepository.findAllActive().stream()
                .map(SiteDto::from)
                .collect(Collectors.toList());
    }

    public SiteDto findByCode(String code) {
        Site site = siteRepository.findByCodeAndNotDeleted(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.SITE_NOT_FOUND));
        return SiteDto.from(site);
    }

    public Site findEntityByCode(String code) {
        return siteRepository.findByCodeAndNotDeleted(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.SITE_NOT_FOUND));
    }

    @Transactional
    public SiteDto create(SiteCreateRequest request) {
        if (siteRepository.existsByCode(request.getCode())) {
            throw new BusinessException(ErrorCode.DUPLICATE_SITE_CODE);
        }

        Site site = request.toEntity();
        Site savedSite = siteRepository.save(site);
        return SiteDto.from(savedSite);
    }

    @Transactional
    public SiteDto update(String code, SiteUpdateRequest request) {
        Site site = siteRepository.findByCodeAndNotDeleted(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.SITE_NOT_FOUND));

        site.update(
                request.getName(),
                request.getDescription(),
                request.getDomain(),
                request.getStatus() != null ? request.getStatus() : site.getStatus(),
                request.getSettings()
        );

        return SiteDto.from(site);
    }

    @Transactional
    public void delete(String code) {
        Site site = siteRepository.findByCodeAndNotDeleted(code)
                .orElseThrow(() -> new BusinessException(ErrorCode.SITE_NOT_FOUND));

        site.delete();
    }

    public boolean existsByCode(String code) {
        return siteRepository.existsByCode(code);
    }
}
