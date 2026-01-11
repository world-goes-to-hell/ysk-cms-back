package com.ysk.cms.domain.atchfile.dto;

import com.ysk.cms.domain.atchfile.entity.AtchFile;
import com.ysk.cms.domain.atchfile.entity.AtchFileType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AtchFileDto {

    private Long id;
    private String siteCode;
    private String originalName;
    private String storedName;
    private String filePath;
    private String url;
    private String mimeType;
    private Long fileSize;
    private AtchFileType type;
    private String description;
    private String altText;
    private Integer width;
    private Integer height;
    private String extension;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AtchFileDto from(AtchFile file, String baseUrl) {
        return AtchFileDto.builder()
                .id(file.getId())
                .siteCode(file.getSite() != null ? file.getSite().getCode() : null)
                .originalName(file.getOriginalName())
                .storedName(file.getStoredName())
                .filePath(file.getFilePath())
                .url("/api/public/files/" + file.getId())
                .mimeType(file.getMimeType())
                .fileSize(file.getFileSize())
                .type(file.getType())
                .description(file.getDescription())
                .altText(file.getAltText())
                .width(file.getWidth())
                .height(file.getHeight())
                .extension(file.getExtension())
                .createdAt(file.getCreatedAt())
                .updatedAt(file.getUpdatedAt())
                .build();
    }
}
