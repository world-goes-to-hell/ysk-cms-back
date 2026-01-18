package com.ysk.cms.domain.admin.user.service;

import com.ysk.cms.common.dto.PageResponse;
import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.admin.user.dto.*;
import com.ysk.cms.domain.admin.user.dto.*;
import com.ysk.cms.domain.admin.user.entity.Role;
import com.ysk.cms.domain.admin.user.entity.User;
import com.ysk.cms.domain.admin.user.entity.UserStatus;
import com.ysk.cms.domain.admin.user.repository.RoleRepository;
import com.ysk.cms.domain.admin.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final int TEMP_PASSWORD_LENGTH = 12;

    @Transactional(readOnly = true)
    public PageResponse<UserDto> getUsers(String roleName, UserStatus status, String keyword, Pageable pageable) {
        Page<User> page = userRepository.findAllWithFilters(roleName, status, keyword, pageable);
        return PageResponse.of(page.map(UserDto::from));
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return UserDto.from(user);
    }

    @Transactional
    public UserDto createUser(CreateUserRequest request) {
        // 중복 검사
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        // 역할 조회
        Role role = roleRepository.findByName(request.getRole())
                .orElseGet(() -> roleRepository.findByName("USER")
                        .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND)));

        // 사용자 생성
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .department(request.getDepartment())
                .position(request.getPosition())
                .status(UserStatus.ACTIVE)
                .build();

        user.addRole(role);
        User savedUser = userRepository.save(user);

        return UserDto.from(savedUser);
    }

    @Transactional
    public UserDto updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 이메일 중복 검사 (다른 사용자가 사용 중인지)
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
            }
        }

        // 프로필 업데이트
        user.updateProfile(
                request.getName() != null ? request.getName() : user.getName(),
                request.getEmail() != null ? request.getEmail() : user.getEmail(),
                request.getPhone() != null ? request.getPhone() : user.getPhone(),
                request.getDepartment() != null ? request.getDepartment() : user.getDepartment(),
                request.getPosition() != null ? request.getPosition() : user.getPosition()
        );

        // 상태 업데이트
        if (request.getStatus() != null) {
            user.updateStatus(request.getStatus());
        }

        // 역할 업데이트
        if (request.getRole() != null) {
            Role newRole = roleRepository.findByName(request.getRole())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));
            user.clearRoles();
            user.addRole(newRole);
        }

        return UserDto.from(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 소프트 삭제
        user.delete();
    }

    @Transactional
    public void updateUserStatus(Long id, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.updateStatus(status);
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 현재 비밀번호 확인 (관리자가 아닌 경우)
        if (request.getCurrentPassword() != null) {
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new BusinessException(ErrorCode.INVALID_PASSWORD);
            }
        }

        user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    @Transactional
    public ResetPasswordResponse resetPassword(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String temporaryPassword = generateTemporaryPassword();
        user.updatePassword(passwordEncoder.encode(temporaryPassword));

        return new ResetPasswordResponse(temporaryPassword);
    }

    private String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        return random.ints(TEMP_PASSWORD_LENGTH, 0, TEMP_PASSWORD_CHARS.length())
                .mapToObj(i -> String.valueOf(TEMP_PASSWORD_CHARS.charAt(i)))
                .collect(Collectors.joining());
    }
}
