package com.ysk.cms.domain.admin.atchfile.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.config.GcsConfig;
import com.ysk.cms.domain.admin.atchfile.dto.AtchFileDto;
import com.ysk.cms.domain.admin.atchfile.dto.AtchFileListDto;
import com.ysk.cms.domain.admin.atchfile.dto.AtchFileUpdateRequest;
import com.ysk.cms.domain.admin.atchfile.dto.AtchFileUploadRequest;
import com.ysk.cms.domain.admin.atchfile.dto.*;
import com.ysk.cms.domain.admin.atchfile.entity.AtchFile;
import com.ysk.cms.domain.admin.atchfile.entity.AtchFileType;
import com.ysk.cms.domain.admin.atchfile.repository.AtchFileRepository;
import com.ysk.cms.domain.admin.site.entity.Site;
import com.ysk.cms.domain.admin.site.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AtchFileService {

    private final AtchFileRepository atchFileRepository;
    private final SiteRepository siteRepository;
    private final Storage storage;
    private final GcsConfig gcsConfig;

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg");
    private static final Set<String> VIDEO_EXTENSIONS = Set.of("mp4", "webm", "avi", "mov", "mkv");
    private static final Set<String> AUDIO_EXTENSIONS = Set.of("mp3", "wav", "ogg", "flac", "aac");
    private static final Set<String> DOCUMENT_EXTENSIONS = Set.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "hwp");
    private static final Set<String> ARCHIVE_EXTENSIONS = Set.of("zip", "rar", "7z", "tar", "gz");

    public PageResponse<AtchFileListDto> getFileList(Pageable pageable) {
        Page<AtchFile> filePage = atchFileRepository.findAllActive(pageable);
        String baseUrl = getBaseUrl();
        return PageResponse.of(filePage.map(f -> AtchFileListDto.from(f, baseUrl)));
    }

    public PageResponse<AtchFileListDto> getFileListBySite(String siteCode, Pageable pageable) {
        Page<AtchFile> filePage = atchFileRepository.findBySiteCode(siteCode, pageable);
        String baseUrl = getBaseUrl();
        return PageResponse.of(filePage.map(f -> AtchFileListDto.from(f, baseUrl)));
    }

    public PageResponse<AtchFileListDto> getFileListBySiteAndType(String siteCode, AtchFileType type, Pageable pageable) {
        Page<AtchFile> filePage = atchFileRepository.findBySiteCodeAndType(siteCode, type, pageable);
        String baseUrl = getBaseUrl();
        return PageResponse.of(filePage.map(f -> AtchFileListDto.from(f, baseUrl)));
    }

    public PageResponse<AtchFileListDto> searchFiles(String siteCode, String keyword, Pageable pageable) {
        Page<AtchFile> filePage = atchFileRepository.searchByKeyword(siteCode, keyword, pageable);
        String baseUrl = getBaseUrl();
        return PageResponse.of(filePage.map(f -> AtchFileListDto.from(f, baseUrl)));
    }

    public AtchFileDto getFile(Long id) {
        AtchFile file = atchFileRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
        return AtchFileDto.from(file, getBaseUrl());
    }

    public AtchFileDto getFileBySite(String siteCode, Long id) {
        AtchFile file = atchFileRepository.findBySiteCodeAndId(siteCode, id)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));
        return AtchFileDto.from(file, getBaseUrl());
    }

    @Transactional
    public AtchFileDto uploadFile(String siteCode, MultipartFile file, AtchFileUploadRequest request) {
        validateFile(file);

        Site site = null;
        if (siteCode != null) {
            site = siteRepository.findByCode(siteCode)
                    .orElseThrow(() -> new BusinessException(ErrorCode.SITE_NOT_FOUND));
        }

        String originalName = file.getOriginalFilename();
        String extension = getExtension(originalName);
        String storedName = generateStoredName(extension);
        String filePath = generateFilePath(siteCode, storedName);
        AtchFileType fileType = determineFileType(extension);

        Integer width = null;
        Integer height = null;
        if (fileType == AtchFileType.IMAGE) {
            try {
                BufferedImage image = ImageIO.read(file.getInputStream());
                if (image != null) {
                    width = image.getWidth();
                    height = image.getHeight();
                }
            } catch (Exception e) {
                log.warn("이미지 크기 읽기 실패: {}", e.getMessage());
            }
        }

        try {
            BlobId blobId = BlobId.of(gcsConfig.getBucketName(), filePath);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType(file.getContentType())
                    .build();
            storage.create(blobInfo, file.getBytes());
            log.info("GCS 파일 업로드 완료: {}", filePath);
        } catch (Exception e) {
            log.error("GCS 파일 업로드 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        AtchFile atchFile = AtchFile.builder()
                .site(site)
                .originalName(originalName)
                .storedName(storedName)
                .filePath(filePath)
                .mimeType(file.getContentType())
                .fileSize(file.getSize())
                .type(fileType)
                .description(request != null ? request.getDescription() : null)
                .altText(request != null ? request.getAltText() : null)
                .width(width)
                .height(height)
                .build();

        AtchFile savedFile = atchFileRepository.save(atchFile);
        return AtchFileDto.from(savedFile, getBaseUrl());
    }

    @Transactional
    public List<AtchFileDto> uploadMultipleFiles(String siteCode, List<MultipartFile> files, AtchFileUploadRequest request) {
        return files.stream()
                .map(file -> uploadFile(siteCode, file, request))
                .toList();
    }

    @Transactional
    public AtchFileDto updateFile(Long id, AtchFileUpdateRequest request) {
        AtchFile file = atchFileRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));

        file.update(request.getDescription(), request.getAltText());
        return AtchFileDto.from(file, getBaseUrl());
    }

    @Transactional
    public void deleteFile(Long id) {
        AtchFile file = atchFileRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));

        try {
            BlobId blobId = BlobId.of(gcsConfig.getBucketName(), file.getFilePath());
            boolean deleted = storage.delete(blobId);
            if (deleted) {
                log.info("GCS 파일 삭제 완료: {}", file.getFilePath());
            } else {
                log.warn("GCS 파일이 존재하지 않음: {}", file.getFilePath());
            }
        } catch (Exception e) {
            log.error("GCS 파일 삭제 실패: {}", e.getMessage());
        }

        file.delete();
    }

    public InputStream downloadFile(Long id) {
        AtchFile file = atchFileRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));

        try {
            BlobId blobId = BlobId.of(gcsConfig.getBucketName(), file.getFilePath());
            Blob blob = storage.get(blobId);
            if (blob == null) {
                throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
            }
            return new ByteArrayInputStream(blob.getContent());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("GCS 파일 다운로드 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    public String getPresignedUrl(Long id) {
        AtchFile file = atchFileRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));

        try {
            BlobId blobId = BlobId.of(gcsConfig.getBucketName(), file.getFilePath());
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
            URL signedUrl = storage.signUrl(blobInfo, 1, TimeUnit.HOURS);
            return signedUrl.toString();
        } catch (Exception e) {
            log.error("Signed URL 생성 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }

        String extension = getExtension(file.getOriginalFilename());
        if (extension.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String generateStoredName(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    private String generateFilePath(String siteCode, String storedName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        if (siteCode != null) {
            return siteCode + "/" + datePath + "/" + storedName;
        }
        return "common/" + datePath + "/" + storedName;
    }

    private AtchFileType determineFileType(String extension) {
        if (IMAGE_EXTENSIONS.contains(extension)) {
            return AtchFileType.IMAGE;
        } else if (VIDEO_EXTENSIONS.contains(extension)) {
            return AtchFileType.VIDEO;
        } else if (AUDIO_EXTENSIONS.contains(extension)) {
            return AtchFileType.AUDIO;
        } else if (DOCUMENT_EXTENSIONS.contains(extension)) {
            return AtchFileType.DOCUMENT;
        } else if (ARCHIVE_EXTENSIONS.contains(extension)) {
            return AtchFileType.ARCHIVE;
        } else {
            return AtchFileType.OTHER;
        }
    }

    private String getBaseUrl() {
        return "https://storage.googleapis.com/" + gcsConfig.getBucketName();
    }
}
