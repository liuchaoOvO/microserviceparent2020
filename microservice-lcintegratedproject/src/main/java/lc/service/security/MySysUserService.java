package lc.service.security;

import lc.dao.security.SysUserMapper;
import lc.entity.SysUser;
import lc.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author liuchaoOvO on 2019/1/7
 */
@Component
public class MySysUserService {
    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    RedisUtil redisUtil;

    //注册用户并授予默认角色权限
    public boolean registUser(SysUser user) {
        try {
            sysUserMapper.registUser(user);
            //给正在注册的用户赋予默认角色权限
            sysUserMapper.grantDefaultRoleAuth(user);
            // msgProducer.sendMsg("注册成功===rabbitmq");
            // msgProducer.sendTopicMsg("success");
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public SysUser findUserByUsername(String username) {
        SysUser sysUser = sysUserMapper.findUserByUsername(username);
        if (sysUser != null) {
            return sysUser;
        }
        return null;
    }

    public boolean lockUser(String username) {
        SysUser sysUser = sysUserMapper.findUserByUsername(username);
        if (sysUser != null) {
            sysUserMapper.lockUser(username);
            return true;
        }
        return false;
    }

    public SysUser getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        SysUser user = (SysUser) redisUtil.get("token:" + token);
        //延长有效期，有效期等于最后一次操作+有效期
        if (user != null) {
            addCookie(response, token, user);
        }
        return user;
    }

    /**
     * 将token做为key，用户信息做为value 存入redis模拟session
     * 同时将token存入cookie，保存登录状态
     */
    private void addCookie(HttpServletResponse response, String token, SysUser sysUser) {
        redisUtil.set("token:" + token, sysUser, 3600 * 24 * 2L);
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(3600 * 24 * 2);
        cookie.setPath("/");//设置为网站根目录
        response.addCookie(cookie);
    }
}
