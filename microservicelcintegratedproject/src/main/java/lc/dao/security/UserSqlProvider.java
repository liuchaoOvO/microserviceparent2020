package lc.dao.security;

import lc.entity.SysUser;
import lc.util.UUIDUtil;
import org.apache.ibatis.annotations.Insert;

/**
 * @author liuchaoOvO on 2019/1/2
 */
public class UserSqlProvider
{
    public String listByUsername(String username){
        return "select * from sys_user where username=#{username}" ;
    }
    public String grantDefaultRoleAuth(SysUser user){
        //生成sys_role_user表的id
       String sruid= UUIDUtil.uuid();
       String userid=user.getId();
        return "insert into sys_role_user(id,sys_user_id,sys_role_id) values('"+sruid+"','"+userid+"','2') ";

    }

}
