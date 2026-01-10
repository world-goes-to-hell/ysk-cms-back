package com.ysk.cms.domain.media.dto;

import com.ysk.cms.domain.media.entity.Media;
import com.ysk.cms.domain.media.entity.MediaType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MediaDto {

    private Long id;
    private String siteCode;
    private String originalName;
    private String storedName;
    private String filePath;
    private String url;
    private String mimeType;
    private Long fileSize;
    private MediaType type;
    private String description;
    private String altText;
    private Integer width;
    private Integer height;
    private String extension;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static MediaDto from(Media media, String baseUrl) {
        return MediaDto.builder()
                .id(media.getId())
                .siteCode(media.getSite() != null ? media.getSite().getCode() : null)
                .originalName(media.getOriginalName())
                .storedName(media.getStoredName())
                .filePath(media.getFilePath())
                .url(baseUrl + "/" + media.getStoredName())
                .mimeType(media.getMimeType())
                .fileSize(media.getFileSize())
                .type(media.getType())
                .description(media.getDescription())
                .altText(media.getAltText())
                .width(media.getWidth())
                .height(media.getHeight())
                .extension(media.getExtension())
                .createdAt(media.getCreatedAt())
                .updatedAt(media.getUpdatedAt())
                .build();
    }
}
