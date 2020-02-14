package lc.config;

import lc.filter.ValidateCodeFilter;
import lc.handler.MyAuthenticationFailureHandler;
import lc.handler.MyAuthenticationSuccessHandler;
import lc.service.security.MyUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by CoderTnT on 2018/7/31.
 */
@Configuration           // 声明为配置类
public class MyWebMvcConfig extends WebMvcConfigurerAdapter
{
    private final static Logger logger = LoggerFactory.getLogger(MyWebMvcConfig.class);
    @Autowired
    SysUserArgumentResolver sysUserArgumentResolver;

    /**
     * SpringMVC框架回调addArgumentResolvers，然后给Controller的参数赋值
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(sysUserArgumentResolver);
        super.addArgumentResolvers(argumentResolvers);
    }
}
