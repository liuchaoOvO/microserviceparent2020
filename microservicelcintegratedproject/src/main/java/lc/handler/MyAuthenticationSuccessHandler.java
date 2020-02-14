package lc.handler;


import lc.entity.SysUser;
import lc.util.CommonUtil;
import lc.util.RedisUtil;
import lc.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

/**
 * Created by CoderTnT on 2018/8/2.
 */

@Component
public class MyAuthenticationSuccessHandler
        extends SimpleUrlAuthenticationSuccessHandler
{
    private final static Logger logger = LoggerFactory.getLogger(MyAuthenticationSuccessHandler.class);
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws ServletException, IOException
    {
        logger.info("认证成功");
        Map map = CommonUtil.threadLocal.get();
        if (map.get("user") != null){
            //生成唯一id作为token
            String token = UUIDUtil.uuid();
            addCookie(response, token, (SysUser) map.get("user"));
        }

      /*  String Url="http://www.baidu.com";
        String reIndex="/index";
       response.sendRedirect(reIndex);*/
        super.onAuthenticationSuccess(request,response,authentication);
    }


    /**
     * 将token做为key，用户信息做为value 存入redis模拟session
     * 同时将token存入cookie，保存登录状态
     */
    private  void addCookie(HttpServletResponse response, String token, SysUser  sysUser) {
        redisUtil.set("token:"+token,sysUser,3600*24 *2L);
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(3600*24 *2);
        cookie.setPath("/");//设置为网站根目录
        response.addCookie(cookie);
    }
}


