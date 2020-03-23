package lc.service.impl;

/**
 * @author liuchaoOvO on 2019/3/15
 */

import com.codingapi.txlcn.tc.annotation.DTXPropagation;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import lc.dao.UserDao;
import lc.entity.SysUser;
import lc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService
{

    @Autowired
    private UserDao userDao;

    @Override
    @LcnTransaction (propagation = DTXPropagation.SUPPORTS) //分布式事务注解
    public boolean addUser(SysUser user) {
        boolean flag;
        //测试 事务异常
        //int v=100/0;
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
