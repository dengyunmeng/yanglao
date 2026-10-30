package com.elderlycare.security;

import com.elderlycare.entity.*;
import com.elderlycare.mapper.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUser>().eq("phone", phone));
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + phone);
        }

        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysUserRole>().eq("user_id", user.getId()));

        List<String> roleCodes = new ArrayList<>();
        List<Long> roleIds = new ArrayList<>();
        for (SysUserRole ur : userRoles) {
            SysRole role = sysRoleMapper.selectById(ur.getRoleId());
            if (role != null) {
                roleCodes.add("ROLE_" + role.getCode());
                roleIds.add(role.getId());
            }
        }

        List<String> permissions = new ArrayList<>();
        if (!roleIds.isEmpty()) {
            List<SysRolePermission> rps = sysRolePermissionMapper.selectList(
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<SysRolePermission>().in("role_id", roleIds));
            List<Long> permIds = rps.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toList());
            if (!permIds.isEmpty()) {
                List<SysPermission> perms = sysPermissionMapper.selectBatchIds(permIds);
                for (SysPermission p : perms) {
                    if (p.getCode() != null) {
                        permissions.add(p.getCode());
                    }
                }
            }
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (String r : roleCodes) authorities.add(new SimpleGrantedAuthority(r));
        for (String p : permissions) authorities.add(new SimpleGrantedAuthority(p));

        return new User(user.getPhone(), user.getPassword(), user.getStatus() == 1, true, true, true, authorities);
    }
}
