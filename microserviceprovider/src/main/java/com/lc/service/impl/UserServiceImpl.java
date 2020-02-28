package com.lc.service.impl;

/**
 * @author liuchaoOvO on 2019/3/15
 */

import com.lc.dao.UserDao;
import com.lc.entity.SysUser;
import com.lc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public boolean addUser(SysUser user) {
        boolean flag;
        flag = userDao.addUser(user);
        return flag;
    }

    @Override
    public SysUser getUser(String id) {
        SysUser user = userDao.getUser(id);
        return user;
    }

    @Override
    public List<SysUser> getUsers() {
        List<SysUser> users = userDao.getUsers();
        return users;
    }

}
