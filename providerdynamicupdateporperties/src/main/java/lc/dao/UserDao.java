package lc.dao;

/**
 * @author liuchaoOvO on 2019/3/15
 */

import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface UserDao {

    public boolean addUser(SysUser user);

    public SysUser getUser(int id);

    public List<SysUser> getUsers();
}
