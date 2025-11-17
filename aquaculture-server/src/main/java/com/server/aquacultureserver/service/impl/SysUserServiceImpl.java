package com.server.aquacultureserver.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.server.aquacultureserver.domain.SysPermission;
import com.server.aquacultureserver.domain.SysRole;
import com.server.aquacultureserver.domain.SysUser;
import com.server.aquacultureserver.dto.LoginDTO;
import com.server.aquacultureserver.dto.UserDTO;
import com.server.aquacultureserver.mapper.SysRoleMapper;
import com.server.aquacultureserver.mapper.SysUserMapper;
import com.server.aquacultureserver.service.SysPermissionService;
import com.server.aquacultureserver.service.SysUserService;
import com.server.aquacultureserver.utils.JwtUtil;
import com.server.aquacultureserver.utils.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务实现类
 */
@Service
public class SysUserServiceImpl implements SysUserService {
    
    @Autowired
    private SysUserMapper userMapper;
    
    @Autowired
    private SysRoleMapper roleMapper;
    
    @Autowired
    private SysPermissionService permissionService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 密码加密
     * 使用MD5算法对密码进行加密
     * @param password 原始密码
     * @return 加密后的密码
     */
    private String encryptPassword(String password) {
        return DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 用户登录
     * 验证用户名和密码，返回用户信息和JWT token
     * @param loginDTO 登录信息（包含用户名和密码）
     * @return 用户信息DTO（包含token、权限列表等）
     */
    @Override
    public UserDTO login(LoginDTO loginDTO) {
        SysUser user = userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, loginDTO.getUsername())
        );
        
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        // 先检查用户状态，再验证密码，这样能给出更准确的提示
        if (user.getStatus() == 0) {
            throw new RuntimeException("用户已被禁用");
        }
        
        // 待审核用户不能登录，优先提示审核状态
        if (user.getStatus() == 2) {
            throw new RuntimeException("您的账号正在审核中，请等待管理员审核通过后再登录");
        }
        
        // 验证密码
        String encryptedPassword = encryptPassword(loginDTO.getPassword());
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        
        UserDTO userDTO = new UserDTO();
        BeanUtils.copyProperties(user, userDTO);
        
        // 查询角色信息
        SysRole role = roleMapper.selectById(user.getRoleId());
        if (role != null) {
            userDTO.setRoleName(role.getRoleName());
        }
        
        // 查询用户权限列表
        List<SysPermission> permissions = permissionService.getPermissionsByRoleId(user.getRoleId());
        List<String> permissionCodes = permissions.stream()
            .map(SysPermission::getPermissionCode)
            .collect(java.util.stream.Collectors.toList());
        userDTO.setPermissions(permissionCodes);
        
        // 设置区域ID和部门ID
        userDTO.setAreaId(user.getAreaId());
        userDTO.setDepartmentId(user.getDepartmentId());
        
        // 生成JWT token
        String token = jwtUtil.generateToken(user.getUserId(), user.getUsername(), user.getRoleId());
        userDTO.setToken(token);
        
        return userDTO;
    }
    
    /**
     * 根据ID查询用户
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public SysUser getById(Long userId) {
        return userMapper.selectById(userId);
    }
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public SysUser getByUsername(String username) {
        return userMapper.selectOne(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username)
        );
    }
    
    /**
     * 分页查询用户列表
     * 根据用户角色进行权限过滤，查询结果会自动填充角色名称
     * @param current 当前页码
     * @param size 每页大小
     * @param username 用户名（模糊查询）
     * @param roleId 角色ID
     * @return 分页结果（包含角色名称）
     */
    @Override
    public Page<SysUser> getPage(Integer current, Integer size, String username, Long roleId) {
        Page<SysUser> page = new Page<>(current, size);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        // 普通操作员只能查看自己创建的用户或自己
        if (UserContext.isOperator()) {
            Long userId = UserContext.getCurrentUserId();
            if (userId != null) {
                wrapper.eq(SysUser::getUserId, userId);
            } else {
                return page;
            }
        }
        // 部门管理员只能查看所属部门下的用户（通过departmentId过滤）
        else if (UserContext.isDepartmentManager()) {
            SysUser currentUser = UserContext.getCurrentUser();
            if (currentUser != null && currentUser.getDepartmentId() != null) {
                // 部门管理员可以查看同一部门的操作员（departmentId相同，且roleId=3）
                // 包括已启用和待审核的操作员
                wrapper.eq(SysUser::getDepartmentId, currentUser.getDepartmentId())
                       .eq(SysUser::getRoleId, 3L); // 操作员
            } else {
                // 如果部门管理员没有分配部门，返回空结果
                return page;
            }
        }
        // 系统管理员可以查看所有用户（不添加额外过滤条件）
        
        if (username != null && !username.isEmpty()) {
            wrapper.like(SysUser::getUsername, username);
        }
        if (roleId != null) {
            wrapper.eq(SysUser::getRoleId, roleId);
        }
        
        wrapper.orderByDesc(SysUser::getCreateTime);
        Page<SysUser> result = userMapper.selectPage(page, wrapper);
        
        // 填充角色名称
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            for (SysUser user : result.getRecords()) {
                if (user.getRoleId() != null) {
                    SysRole role = roleMapper.selectById(user.getRoleId());
                    if (role != null) {
                        user.setRoleName(role.getRoleName());
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * 新增用户
     * 根据用户角色进行权限控制，自动加密密码
     * 如果是注册（status未设置），默认状态为待审核（2）
     * @param user 用户信息
     * @return 是否成功
     */
    @Override
    public boolean saveUser(SysUser user) {
        // 检查用户名是否已存在
        SysUser existUser = getByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 部门管理员权限检查：只能创建本部门的操作员
        if (UserContext.isDepartmentManager()) {
            SysUser currentUser = UserContext.getCurrentUser();
            if (currentUser != null && currentUser.getDepartmentId() != null) {
                // 部门管理员只能创建操作员（roleId=3），且必须属于同一部门
                if (user.getRoleId() == null || user.getRoleId() != 3L) {
                    throw new RuntimeException("部门管理员只能创建操作员角色");
                }
                // 确保部门ID匹配
                if (user.getDepartmentId() == null || !user.getDepartmentId().equals(currentUser.getDepartmentId())) {
                    throw new RuntimeException("部门管理员只能为本部门创建用户");
                }
            } else {
                throw new RuntimeException("部门管理员未分配部门，无法创建用户");
            }
        }
        
        // 检查密码是否为空（新增用户时密码是必填的）
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        
        // 加密密码
        user.setPassword(encryptPassword(user.getPassword()));
        
        // 如果是注册（不是管理员创建），设置状态为待审核（2）
        // 管理员创建用户时，status应该已经设置好了（1-启用）
        if (user.getStatus() == null) {
            user.setStatus(2); // 2-待审核
            // 注册时不设置区域，由管理员审核时分配
            user.setAreaId(null);
        }
        
        return userMapper.insert(user) > 0;
    }
    
    /**
     * 更新用户信息
     * 根据用户角色进行权限控制，如果更新了用户名会检查是否重复
     * @param user 用户信息
     * @return 是否成功
     */
    @Override
    public boolean updateUser(SysUser user) {
        // 部门管理员权限检查：只能编辑本部门的操作员
        if (UserContext.isDepartmentManager()) {
            SysUser currentUser = UserContext.getCurrentUser();
            SysUser targetUser = getById(user.getUserId());
            
            if (currentUser == null || currentUser.getDepartmentId() == null) {
                throw new RuntimeException("部门管理员未分配部门，无法编辑用户");
            }
            
            if (targetUser == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 检查目标用户是否为系统管理员（系统管理员不允许编辑）
            if (targetUser.getRoleId() != null && targetUser.getRoleId() == 1L) {
                throw new RuntimeException("系统管理员不允许编辑");
            }
            
            // 部门管理员只能编辑本部门的操作员
            if (targetUser.getDepartmentId() == null || !targetUser.getDepartmentId().equals(currentUser.getDepartmentId())) {
                throw new RuntimeException("部门管理员只能编辑本部门的操作员");
            }
            
            // 如果修改了角色，确保只能设置为操作员
            if (user.getRoleId() != null && user.getRoleId() != 3L) {
                throw new RuntimeException("部门管理员只能将用户角色设置为操作员");
            }
            
            // 如果修改了部门ID，确保仍然是本部门
            if (user.getDepartmentId() != null && !user.getDepartmentId().equals(currentUser.getDepartmentId())) {
                throw new RuntimeException("部门管理员不能将用户分配到其他部门");
            }
        }
        
        // 如果更新了用户名，检查是否重复
        if (user.getUsername() != null) {
            SysUser existUser = getByUsername(user.getUsername());
            if (existUser != null && !existUser.getUserId().equals(user.getUserId())) {
                throw new RuntimeException("用户名已存在");
            }
        }
        
        return userMapper.updateById(user) > 0;
    }
    
    /**
     * 删除用户（物理删除）
     * 根据用户角色进行权限控制
     * @param userId 用户ID
     * @return 是否成功
     */
    @Override
    public boolean deleteUser(Long userId) {
        // 部门管理员权限检查：只能删除本部门的操作员
        if (UserContext.isDepartmentManager()) {
            SysUser currentUser = UserContext.getCurrentUser();
            SysUser targetUser = getById(userId);
            
            if (currentUser == null || currentUser.getDepartmentId() == null) {
                throw new RuntimeException("部门管理员未分配部门，无法删除用户");
            }
            
            if (targetUser == null) {
                throw new RuntimeException("用户不存在");
            }
            
            // 检查目标用户是否为系统管理员（系统管理员不允许删除）
            if (targetUser.getRoleId() != null && targetUser.getRoleId() == 1L) {
                throw new RuntimeException("系统管理员不允许删除");
            }
            
            // 部门管理员只能删除本部门的操作员
            if (targetUser.getDepartmentId() == null || !targetUser.getDepartmentId().equals(currentUser.getDepartmentId())) {
                throw new RuntimeException("部门管理员只能删除本部门的操作员");
            }
        }
        
        return userMapper.deleteById(userId) > 0;
    }
    
    /**
     * 修改密码
     * 如果提供了原密码，会先验证原密码是否正确
     * @param userId 用户ID
     * @param oldPassword 原密码（可选）
     * @param newPassword 新密码
     * @return 是否成功
     */
    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 如果提供了原密码，则验证原密码
        if (oldPassword != null && !oldPassword.trim().isEmpty()) {
            String encryptedOldPassword = encryptPassword(oldPassword);
            if (!encryptedOldPassword.equals(user.getPassword())) {
                throw new RuntimeException("原密码错误");
            }
        }
        // 如果没有提供原密码，则跳过原密码验证（仅限修改自己的密码，已在Controller层验证）
        
        // 加密新密码并更新
        user.setPassword(encryptPassword(newPassword));
        return userMapper.updateById(user) > 0;
    }
    
    /**
     * 重置密码
     * 根据用户角色进行权限控制，不需要原密码验证
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 是否成功
     */
    @Override
    public boolean resetPassword(Long userId, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 部门管理员权限检查：只能重置本部门的操作员密码
        if (UserContext.isDepartmentManager()) {
            SysUser currentUser = UserContext.getCurrentUser();
            if (currentUser == null || currentUser.getDepartmentId() == null) {
                throw new RuntimeException("部门管理员未分配部门，无法重置密码");
            }
            
            // 检查目标用户是否为系统管理员（系统管理员不允许重置密码）
            if (user.getRoleId() != null && user.getRoleId() == 1L) {
                throw new RuntimeException("系统管理员不允许重置密码");
            }
            
            // 部门管理员只能重置本部门的操作员密码
            if (user.getDepartmentId() == null || !user.getDepartmentId().equals(currentUser.getDepartmentId())) {
                throw new RuntimeException("部门管理员只能重置本部门的操作员密码");
            }
        }
        
        // 加密新密码并更新
        user.setPassword(encryptPassword(newPassword));
        return userMapper.updateById(user) > 0;
    }
    
    /**
     * 统计所有用户数量
     * @return 用户数量
     */
    @Override
    public long count() {
        return userMapper.selectCount(null);
    }
    
    /**
     * 审批用户注册申请
     * 根据用户角色进行权限控制：
     * - 部门管理员只能由系统管理员审批
     * - 操作员可以由系统管理员或该部门的部门管理员审批
     * 审核通过时会根据角色设置部门ID或区域ID
     * @param userId 用户ID
     * @param status 审批状态（1-通过，0-拒绝）
     * @param departmentId 部门ID（审核通过时设置）
     * @param areaId 区域ID（审核通过时设置，用于操作员）
     * @param remark 备注
     * @return 是否成功
     */
    @Override
    public boolean approveUser(Long userId, Integer status, Long departmentId, Long areaId, String remark) {
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        if (user.getStatus() != 2) {
            throw new RuntimeException("该用户不是待审核状态");
        }
        
        // 检查审批权限
        // 如果是部门管理员角色（roleId=5），只能由系统管理员审批
        // 如果是操作员角色（roleId=3），可以由系统管理员或该部门的部门管理员审批
        Long userRoleId = user.getRoleId();
        boolean isAdmin = UserContext.isAdmin();
        boolean isDepartmentManager = UserContext.isDepartmentManager();
        
        if (userRoleId != null && userRoleId == 5L) {
            // 部门管理员只能由系统管理员审批
            if (!isAdmin) {
                throw new RuntimeException("部门管理员只能由系统管理员审批");
            }
        } else if (userRoleId != null && userRoleId == 3L) {
            // 操作员可以由系统管理员或该部门的部门管理员审批
            if (!isAdmin) {
                if (!isDepartmentManager) {
                    throw new RuntimeException("您没有权限审批该用户");
                }
                // 检查部门管理员是否有权限审批（需要验证部门是否匹配）
                // 这里假设注册时已经设置了departmentId，需要验证当前部门管理员的部门是否匹配
                Long currentUserDepartmentId = UserContext.getCurrentUser() != null 
                    ? UserContext.getCurrentUser().getDepartmentId() : null;
                Long targetDepartmentId = departmentId != null ? departmentId : 
                    (user.getDepartmentId() != null ? user.getDepartmentId() : null);
                
                if (currentUserDepartmentId == null || targetDepartmentId == null 
                    || !currentUserDepartmentId.equals(targetDepartmentId)) {
                    throw new RuntimeException("您只能审批本部门下的操作员");
                }
            }
        }
        
        // 更新用户状态
        user.setStatus(status); // 1-通过，0-拒绝
        
        // 根据角色设置部门或区域
        if (status == 1) { // 审核通过
            if (userRoleId != null && userRoleId == 5L) {
                // 部门管理员：设置部门ID
                Long finalDepartmentId = departmentId != null ? departmentId : user.getDepartmentId();
                if (finalDepartmentId != null) {
                    user.setDepartmentId(finalDepartmentId);
                    // 部门管理员需要通过areaId找到对应的部门区域作为默认区域
                    // 这里简化处理，可以选择该部门下的第一个区域，或者由管理员指定
                }
            } else if (userRoleId != null && userRoleId == 3L) {
                // 操作员：设置区域ID
                Long finalAreaId = areaId != null ? areaId : user.getAreaId();
                if (finalAreaId != null) {
                    user.setAreaId(finalAreaId);
                }
                // 如果指定了部门ID，也需要设置（通过区域可以关联到部门）
                if (departmentId != null) {
                    user.setDepartmentId(departmentId);
                }
            }
        }
        
        return userMapper.updateById(user) > 0;
    }
    
    /**
     * 获取所有系统管理员的用户ID列表
     * @return 系统管理员用户ID列表
     */
    @Override
    public List<Long> getAllAdminUserIds() {
        List<SysUser> adminUsers = userMapper.selectList(
            new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRoleId, 1) // 系统管理员
                .eq(SysUser::getStatus, 1) // 已启用
        );
        return adminUsers.stream()
            .map(SysUser::getUserId)
            .collect(Collectors.toList());
    }
    
    /**
     * 查询待审核用户列表
     * 根据用户角色进行权限过滤，查询结果会自动填充角色名称
     * @return 待审核用户列表（包含角色名称）
     */
    @Override
    public List<SysUser> getPendingUsers() {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getStatus, 2); // 待审核
        
        // 普通操作员不能查看待审核用户
        if (UserContext.isOperator()) {
            return Collections.emptyList();
        }
        // 部门管理员只能查看所属部门下的待审核操作员
        else if (UserContext.isDepartmentManager()) {
            SysUser currentUser = UserContext.getCurrentUser();
            if (currentUser != null && currentUser.getDepartmentId() != null) {
                // 只能查看同一部门的待审核操作员（departmentId相同，roleId=3）
                wrapper.eq(SysUser::getDepartmentId, currentUser.getDepartmentId())
                       .eq(SysUser::getRoleId, 3L); // 操作员
            } else {
                // 如果部门管理员没有分配部门，返回空结果
                return Collections.emptyList();
            }
        }
        // 系统管理员可以查看所有待审核用户（不添加额外过滤条件）
        
        wrapper.orderByDesc(SysUser::getCreateTime);
        List<SysUser> users = userMapper.selectList(wrapper);
        
        // 填充角色名称
        for (SysUser user : users) {
            if (user.getRoleId() != null) {
                SysRole role = roleMapper.selectById(user.getRoleId());
                if (role != null) {
                    user.setRoleName(role.getRoleName());
                }
            }
        }
        
        return users;
    }
    
    /**
     * 获取用户列表（用于下拉选择）
     * 根据用户角色进行权限过滤，只返回已启用的用户
     * @param areaId 区域ID（可选，用于进一步过滤）
     * @return 用户列表（只包含userId、username、realName、areaId字段）
     */
    @Override
    public List<SysUser> getUserListForSelect(Long areaId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        
        // 只返回已启用的用户
        wrapper.eq(SysUser::getStatus, 1);
        
        // 普通操作员只能看到自己区域的用户
        if (UserContext.isOperator()) {
            Long userAreaId = UserContext.getCurrentUserAreaId();
            if (userAreaId != null) {
                wrapper.eq(SysUser::getAreaId, userAreaId);
            } else {
                return Collections.emptyList();
            }
        }
        // 部门管理员只能看到所属部门下的操作员
        else if (UserContext.isDepartmentManager()) {
            SysUser currentUser = UserContext.getCurrentUser();
            if (currentUser != null && currentUser.getDepartmentId() != null) {
                // 只能查看同一部门的操作员（departmentId相同，roleId=3）
                wrapper.eq(SysUser::getDepartmentId, currentUser.getDepartmentId())
                       .eq(SysUser::getRoleId, 3L); // 操作员
                // 如果指定了区域，还需要进一步过滤
                if (areaId != null) {
                    wrapper.eq(SysUser::getAreaId, areaId);
                }
            } else {
                return Collections.emptyList();
            }
        }
        // 系统管理员可以指定区域查看用户
        else if (areaId != null) {
            wrapper.eq(SysUser::getAreaId, areaId);
        }
        
        wrapper.select(SysUser::getUserId, SysUser::getUsername, SysUser::getRealName, SysUser::getAreaId)
               .orderByDesc(SysUser::getCreateTime);
        return userMapper.selectList(wrapper);
    }
}

