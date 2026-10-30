package com.elderlycare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.elderlycare.common.BusinessException;
import com.elderlycare.common.Result;
import com.elderlycare.dto.UserChangePasswordDTO;
import com.elderlycare.dto.UserUpdateDTO;
import com.elderlycare.entity.SysRole;
import com.elderlycare.entity.SysUser;
import com.elderlycare.entity.SysUserRole;
import com.elderlycare.mapper.SysRoleMapper;
import com.elderlycare.mapper.SysUserMapper;
import com.elderlycare.mapper.SysUserRoleMapper;
import com.elderlycare.service.UserService;
import com.elderlycare.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public Result<UserVO> updateProfile(Long userId, UserUpdateDTO dto) {
        SysUser u = sysUserMapper.selectById(userId);
        if (u == null) throw new BusinessException(404, "用户不存在");
        if (dto.getName() != null) u.setName(dto.getName());
        if (dto.getAvatar() != null) u.setAvatar(dto.getAvatar());
        sysUserMapper.updateById(u);
        return Result.success(buildVO(u));
    }

    @Override
    public Result<Void> changePassword(Long userId, UserChangePasswordDTO dto) {
        SysUser u = sysUserMapper.selectById(userId);
        if (u == null) throw new BusinessException(404, "用户不存在");
        if (!passwordEncoder.matches(dto.getOldPassword(), u.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }
        u.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        sysUserMapper.updateById(u);
        return Result.success();
    }

    private UserVO buildVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setName(user.getName());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setStatus(user.getStatus());

        List<SysUserRole> urs = sysUserRoleMapper.selectList(new QueryWrapper<SysUserRole>().eq("user_id", user.getId()));
        List<String> roles = new ArrayList<>();
        for (SysUserRole ur : urs) {
            SysRole r = sysRoleMapper.selectById(ur.getRoleId());
            if (r != null && r.getCode() != null) roles.add("ROLE_" + r.getCode());
        }
        vo.setRoles(roles);
        return vo;
    }
}
