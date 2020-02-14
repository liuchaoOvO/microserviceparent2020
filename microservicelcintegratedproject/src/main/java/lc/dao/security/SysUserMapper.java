package lc.dao.security;


import lc.entity.SysRole;
import lc.entity.SysUser;
import lc.util.UUIDUtil;
import org.apache.ibatis.annotations.*;

import java.util.List;


/**
 * Created by liuch on 2018/7/31.
 */
@Mapper
public interface SysUserMapper {

    SysUser findUserByUsername(String username);


    @Insert({"insert into sys_user(id,username,password,status)  values(#{id},#{username},#{password},#{status})"})
    Boolean registUser(SysUser user)throws Exception;
    @Update("update sys_user su set su.status=false where su.username=#{username}")
    void lockUser(String username);
    @Select("select su.status from sys_user su where su.username=#{username}")
    Boolean findUserStatusByUsername(String username);
    @Select("SELECT    sr.* from sys_role_user sru,sys_user su,sys_role sr where su.id=sru.sys_user_id and sru.sys_role_id=sr.id  and  su.username=#{username}")
    List<SysRole> findRolesByUsername(String username);

    /**
     * 方式2：使用注解指定某个工具类的方法来动态编写SQL.
     */
    @SelectProvider(type = UserSqlProvider.class, method = "listByUsername")
    SysUser findUserByUsernameProvider(String username);
    /**
     * 默认插入sys_role_user中间表中 角色为ROLE_USER
     */
   @InsertProvider(type = UserSqlProvider.class, method = "grantDefaultRoleAuth")


     boolean grantDefaultRoleAuth(SysUser user)throws Exception;
}