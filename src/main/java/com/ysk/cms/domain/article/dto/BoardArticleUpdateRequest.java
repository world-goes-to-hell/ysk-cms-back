package com.ysk.cms.domain.article.dto;

import com.ysk.cms.domain.article.entity.ArticleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class BoardArticleUpdateRequest {

    @NotBlank(message = "제목은 필수입니다")
    @Size(max = 300, message = "제목은 300자 이하여야 합니다")
    private String title;

    private String content;

    @Size(max = 50, message = "작성자명은 50자 이하여야 합니다")
    private String author;

    private Boolean isPinned;

    private Boolean isSecret;

    private ArticleStatus status;

    private String answer;

    private List<Long> attachmentIds;
}
