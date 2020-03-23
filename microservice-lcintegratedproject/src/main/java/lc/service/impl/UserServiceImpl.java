package lc.service.impl;

import com.github.pagehelper.Page;
import lc.dao.user.UserDao;
import lc.entity.SysUser;
import lc.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuchaoOvO on 2019/4/16
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    public boolean addUser(SysUser user) {
        boolean flag;
        try {
            userDao.addUser(user);
            flag = true;
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    @Override
    public SysUser getUser(String id) {
        return null;
    }

    @Override
    public List<SysUser> getUsers() {
        return null;
    }


    @Override
    public Page<SysUser> getUserList(Integer pageNum, Integer pageSize) {
        logger.info("pageNum:" + pageNum + "pageSize:" + pageSize);
        return userDao.getUserList(pageNum, pageSize);
    }
}
