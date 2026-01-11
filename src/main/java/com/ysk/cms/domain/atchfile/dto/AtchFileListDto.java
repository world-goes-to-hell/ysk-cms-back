package com.ysk.cms.domain.atchfile.dto;

import com.ysk.cms.domain.atchfile.entity.AtchFile;
import com.ysk.cms.domain.atchfile.entity.AtchFileType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AtchFileListDto {

    private Long id;
    private String originalName;
    private String url;
    private String mimeType;
    private Long fileSize;
    private AtchFileType type;
    private String extension;
    private LocalDateTime createdAt;

    public static AtchFileListDto from(AtchFile file, String baseUrl) {
        return AtchFileListDto.builder()
                .id(file.getId())
                .originalName(file.getOriginalName())
                .url("/api/public/files/" + file.getId())
                .mimeType(file.getMimeType())
                .fileSize(file.getFileSize())
                .type(file.getType())
                .extension(file.getExtension())
                .createdAt(file.getCreatedAt())
                .build();
    }
}
