package com.ysk.cms.domain.media.service;

import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.config.MinioConfig;
import com.ysk.cms.domain.media.dto.*;
import com.ysk.cms.domain.media.entity.Media;
import com.ysk.cms.domain.media.entity.MediaType;
import com.ysk.cms.domain.media.repository.MediaRepository;
import com.ysk.cms.domain.site.entity.Site;
import com.ysk.cms.domain.site.repository.SiteRepository;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
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
public class MediaService {

    private final MediaRepository mediaRepository;
    private final SiteRepository siteRepository;
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    private static final Set<String> IMAGE_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp", "bmp", "svg");
    private static final Set<String> VIDEO_EXTENSIONS = Set.of("mp4", "webm", "avi", "mov", "mkv");
    private static final Set<String> AUDIO_EXTENSIONS = Set.of("mp3", "wav", "ogg", "flac", "aac");
    private static final Set<String> DOCUMENT_EXTENSIONS = Set.of("pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx", "txt", "hwp");
    private static final Set<String> ARCHIVE_EXTENSIONS = Set.of("zip", "rar", "7z", "tar", "gz");

    public PageResponse<MediaListDto> getMediaList(Pageable pageable) {
        Page<Media> mediaPage = mediaRepository.findAllActive(pageable);
        String baseUrl = getBaseUrl();
        return PageResponse.of(mediaPage.map(m -> MediaListDto.from(m, baseUrl)));
    }

    public PageResponse<MediaListDto> getMediaListBySite(String siteCode, Pageable pageable) {
        Page<Media> mediaPage = mediaRepository.findBySiteCode(siteCode, pageable);
        String baseUrl = getBaseUrl();
        return PageResponse.of(mediaPage.map(m -> MediaListDto.from(m, baseUrl)));
    }

    public PageResponse<MediaListDto> getMediaListBySiteAndType(String siteCode, MediaType type, Pageable pageable) {
        Page<Media> mediaPage = mediaRepository.findBySiteCodeAndType(siteCode, type, pageable);
        String baseUrl = getBaseUrl();
        return PageResponse.of(mediaPage.map(m -> MediaListDto.from(m, baseUrl)));
    }

    public PageResponse<MediaListDto> searchMedia(String siteCode, String keyword, Pageable pageable) {
        Page<Media> mediaPage = mediaRepository.searchByKeyword(siteCode, keyword, pageable);
        String baseUrl = getBaseUrl();
        return PageResponse.of(mediaPage.map(m -> MediaListDto.from(m, baseUrl)));
    }

    public MediaDto getMedia(Long id) {
        Media media = mediaRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDIA_NOT_FOUND));
        return MediaDto.from(media, getBaseUrl());
    }

    public MediaDto getMediaBySite(String siteCode, Long id) {
        Media media = mediaRepository.findBySiteCodeAndId(siteCode, id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDIA_NOT_FOUND));
        return MediaDto.from(media, getBaseUrl());
    }

    @Transactional
    public MediaDto uploadMedia(String siteCode, MultipartFile file, MediaUploadRequest request) {
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
        MediaType mediaType = determineMediaType(extension);

        Integer width = null;
        Integer height = null;
        if (mediaType == MediaType.IMAGE) {
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
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(filePath)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO 파일 업로드 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
        }

        Media media = Media.builder()
                .site(site)
                .originalName(originalName)
                .storedName(storedName)
                .filePath(filePath)
                .mimeType(file.getContentType())
                .fileSize(file.getSize())
                .type(mediaType)
                .description(request != null ? request.getDescription() : null)
                .altText(request != null ? request.getAltText() : null)
                .width(width)
                .height(height)
                .build();

        Media savedMedia = mediaRepository.save(media);
        return MediaDto.from(savedMedia, getBaseUrl());
    }

    @Transactional
    public List<MediaDto> uploadMultipleMedia(String siteCode, List<MultipartFile> files, MediaUploadRequest request) {
        return files.stream()
                .map(file -> uploadMedia(siteCode, file, request))
                .toList();
    }

    @Transactional
    public MediaDto updateMedia(Long id, MediaUpdateRequest request) {
        Media media = mediaRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDIA_NOT_FOUND));

        media.update(request.getDescription(), request.getAltText());
        return MediaDto.from(media, getBaseUrl());
    }

    @Transactional
    public void deleteMedia(Long id) {
        Media media = mediaRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDIA_NOT_FOUND));

        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(media.getFilePath())
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO 파일 삭제 실패: {}", e.getMessage());
        }

        media.delete();
    }

    public InputStream downloadMedia(Long id) {
        Media media = mediaRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDIA_NOT_FOUND));

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(media.getFilePath())
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO 파일 다운로드 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.MEDIA_NOT_FOUND);
        }
    }

    public String getPresignedUrl(Long id) {
        Media media = mediaRepository.findByIdAndNotDeleted(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEDIA_NOT_FOUND));

        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(media.getFilePath())
                            .method(Method.GET)
                            .expiry(1, TimeUnit.HOURS)
                            .build()
            );
        } catch (Exception e) {
            log.error("Presigned URL 생성 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.MEDIA_NOT_FOUND);
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

    private MediaType determineMediaType(String extension) {
        if (IMAGE_EXTENSIONS.contains(extension)) {
            return MediaType.IMAGE;
        } else if (VIDEO_EXTENSIONS.contains(extension)) {
            return MediaType.VIDEO;
        } else if (AUDIO_EXTENSIONS.contains(extension)) {
            return MediaType.AUDIO;
        } else if (DOCUMENT_EXTENSIONS.contains(extension)) {
            return MediaType.DOCUMENT;
        } else if (ARCHIVE_EXTENSIONS.contains(extension)) {
            return MediaType.ARCHIVE;
        } else {
            return MediaType.OTHER;
        }
    }

    private String getBaseUrl() {
        return minioConfig.getEndpoint() + "/" + minioConfig.getBucketName();
    }
}
