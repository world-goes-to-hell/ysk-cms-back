package com.ysk.cms.domain.user.dto;

import com.ysk.cms.domain.user.entity.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateUserRequest {

    @Size(max = 50, message = "이름은 50자 이하여야 합니다")
    private String name;

    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;

    private String role;

    private UserStatus status;

    @Size(max = 20, message = "전화번호는 20자 이하여야 합니다")
    private String phone;

    @Size(max = 100, message = "부서명은 100자 이하여야 합니다")
    private String department;

    @Size(max = 100, message = "직책은 100자 이하여야 합니다")
    private String position;
}
