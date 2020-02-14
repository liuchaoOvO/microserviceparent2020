package lc.dao.user;

import com.github.pagehelper.Page;
import lc.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author liuchaoOvO on 2019/4/16
 */
@Mapper
public interface UserDao {

     void addUser (SysUser user);

     SysUser getUser(int id);

     List<SysUser> getUsers();

     @Select("SELECT * FROM sys_user ")
     Page<SysUser> getUserList(Integer pageNum, Integer pageSize);
}