package lc.service.security;

import lc.dao.security.SysUserMapper;
import lc.entity.SysRole;
import lc.entity.SysUser;
import lc.mqproducer.MsgProducer;
import lc.util.CommonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liuchaoOvO on 2018/7/31.
 */
@Component
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private MsgProducer msgProducer;

    /*//注册用户
    public boolean registUser(SysUser user){
        if(stringRedisService.get(user.getUsername()) != null){
            //msgProducer.sendMsg("注册失败===rabbitmq");
            msgProducer.sendTopicMsg("fail");
            return false;
        }else{
            sysUserMapper.registUser(user);
            stringRedisService.set(user.getUsername(),user);
           // msgProducer.sendMsg("注册成功===rabbitmq");
           // msgProducer.sendTopicMsg("success");
            msgProducer.sendFanoutMsgs("sendFanoutMsgs===");
            return true;
        }
    }*/
   /* //分页查询所有用户
    public Page<SysUser> getUserList(Integer pageNum, Integer pageSize){
        PageHelper.startPage(pageNum, pageSize);
        Page<SysUser> userList= sysUserMapper.getUserList();
        for(SysUser user:userList){
            System.out.println("姓名为==="+user.getUsername()+"====");
        }
        return userList;
    }
    public SysUser findUserByUsername(String username){
         SysUser sysUser=sysUserMapper.findUserByUsername(username);
        if(sysUser!=null){
            return sysUser;
        }
        return null;
    }
    public boolean lockUser(String username){
        SysUser sysUser=sysUserMapper.findUserByUsername(username);
        if(sysUser!=null)
        {
            sysUserMapper.lockUser(username);
            return true;
        }
        return false;
    }
    //重写UserDetailsService的UserDetails方法@param username*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user;
        // 从数据库取user
         /* 1,使用特定注解方式
        user = sysUserMapper.findUserByUsername(username);
         */
        //2,使用注解指定某个工具类的方法来动态编写SQL.
        user = sysUserMapper.findUserByUsernameProvider(username);
        if (user == null)
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", username));
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        List<SysRole> sysRoles = sysUserMapper.findRolesByUsername(user.getUsername());
        for (SysRole sysRole : sysRoles) {
            //封装用户信息和角色信息 到 SecurityContextHolder全局缓存中
            grantedAuthorities.add(new SimpleGrantedAuthority(sysRole.getName()));
        }
        for (int i = 0; i < grantedAuthorities.size(); i++) {
            System.out.println("权限:" + grantedAuthorities.get(i));
        }
        Boolean status = sysUserMapper.findUserStatusByUsername(username);
        Map<String, SysUser> map = new ConcurrentHashMap();
        map.put("user", user);
        //放入线程本地类中
        CommonUtil.threadLocal.set(map);
        return new User(user.getUsername(), user.getPassword(), true, true, true, status, grantedAuthorities);
    }
}
