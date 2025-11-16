package com.server.aquacultureserver.dto;

import lombok.Data;

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
    private String phone;
    private String address;
    private String avatar;
    private Integer status;
}

