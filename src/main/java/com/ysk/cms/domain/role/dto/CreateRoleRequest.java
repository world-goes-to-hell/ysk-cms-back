package com.ysk.cms.domain.role.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRoleRequest {

    @NotBlank(message = "역할명은 필수입니다")
    @Size(max = 50, message = "역할명은 50자 이하로 입력해주세요")
    private String name;

    @Size(max = 200, message = "설명은 200자 이하로 입력해주세요")
    private String description;
}
