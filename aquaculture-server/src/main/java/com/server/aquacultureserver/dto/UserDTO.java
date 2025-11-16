package com.server.aquacultureserver.dto;

import lombok.Data;
import java.util.List;

/**
 * 用户DTO
 */
@Data
public class UserDTO {
    private Long userId;
    private String username;
    private String realName;
    private Long roleId;
    private String roleName;
    private Long farmId;
    private Long areaId;
    private String phone;
    private String address;
    private String avatar;
    private Integer status;
    private String token;
    private List<String> permissions; // 用户权限列表（权限代码）
}

