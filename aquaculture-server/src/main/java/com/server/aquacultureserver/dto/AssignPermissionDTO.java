package com.server.aquacultureserver.dto;

import lombok.Data;
import java.util.List;

/**
 * 分配权限DTO
 */
@Data
public class AssignPermissionDTO {
    private Long roleId;
    private List<Long> permissionIds;
}

