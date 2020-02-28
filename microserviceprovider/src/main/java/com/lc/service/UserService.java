package com.lc.service;

/**
 * @author liuchaoOvO on 2019/3/15
 */

import com.lc.entity.SysUser;

import java.util.List;

public interface UserService {

    public boolean addUser(SysUser user);

    public SysUser getUser(String id);

    public List<SysUser> getUsers();
}
