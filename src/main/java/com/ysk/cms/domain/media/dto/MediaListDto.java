package com.ysk.cms.domain.media.dto;

import com.ysk.cms.domain.media.entity.Media;
import com.ysk.cms.domain.media.entity.MediaType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MediaListDto {

    private Long id;
    private String originalName;
    private String url;
    private String mimeType;
    private Long fileSize;
    private MediaType type;
    private String extension;
    private LocalDateTime createdAt;

    public static MediaListDto from(Media media, String baseUrl) {
        return MediaListDto.builder()
                .id(media.getId())
                .originalName(media.getOriginalName())
                .url(baseUrl + "/" + media.getStoredName())
                .mimeType(media.getMimeType())
                .fileSize(media.getFileSize())
                .type(media.getType())
                .extension(media.getExtension())
                .createdAt(media.getCreatedAt())
                .build();
    }
}
