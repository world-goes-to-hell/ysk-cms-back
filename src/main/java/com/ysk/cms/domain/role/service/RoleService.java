package com.ysk.cms.domain.role.service;

import com.ysk.cms.common.exception.BusinessException;
import com.ysk.cms.common.exception.ErrorCode;
import com.ysk.cms.domain.role.dto.CreateRoleRequest;
import com.ysk.cms.domain.role.dto.RoleDto;
import com.ysk.cms.domain.role.dto.UpdateRoleRequest;
import com.ysk.cms.domain.user.entity.Role;
import com.ysk.cms.domain.user.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final RoleRepository roleRepository;

    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleDto::from)
                .toList();
    }

    public RoleDto getRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "역할을 찾을 수 없습니다."));
        return RoleDto.from(role);
    }

    @Transactional
    public RoleDto createRole(CreateRoleRequest request) {
        // 중복 체크
        if (roleRepository.existsByName(request.getName())) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 역할명입니다.");
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Role savedRole = roleRepository.save(role);
        return RoleDto.from(savedRole);
    }

    @Transactional
    public RoleDto updateRole(Long id, UpdateRoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "역할을 찾을 수 없습니다."));

        // 이름 변경 시 중복 체크
        if (request.getName() != null && !request.getName().equals(role.getName())) {
            if (roleRepository.existsByName(request.getName())) {
                throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "이미 존재하는 역할명입니다.");
            }
        }

        role.update(request.getName(), request.getDescription());

        return RoleDto.from(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "역할을 찾을 수 없습니다."));

        // 시스템 기본 역할은 삭제 불가
        if (isSystemRole(role.getName())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "시스템 기본 역할은 삭제할 수 없습니다.");
        }

        roleRepository.delete(role);
    }

    private boolean isSystemRole(String roleName) {
        return "SUPER_ADMIN".equals(roleName) ||
               "ADMIN".equals(roleName) ||
               "SITE_ADMIN".equals(roleName) ||
               "EDITOR".equals(roleName) ||
               "VIEWER".equals(roleName);
    }
}
