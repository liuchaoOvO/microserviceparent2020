package lc.service;

/**
 * @author liuchaoOvO on 2019/3/15
 */

import java.util.List;

public interface UserService {

    public boolean addUser(SysUser user);

    public SysUser getUser(int id);

    public List<SysUser> getUsers();
}
