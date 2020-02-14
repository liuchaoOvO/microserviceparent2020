package lc.service.user;

import com.github.pagehelper.Page;
import lc.entity.SysUser;

import java.util.List;

/**
 * @author liuchaoOvO on 2019/4/16
 */
public interface UserService
{
     boolean addUser (SysUser user);

     SysUser getUser(String id);

     List<SysUser> getUsers();

     Page<SysUser> getUserList(Integer pageNum, Integer pageSize);
}
