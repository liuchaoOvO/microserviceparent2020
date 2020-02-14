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

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by CoderTnT on 2018/7/31.
 */
@Configuration           // 声明为配置类
@EnableWebSecurity       // 启用 Spring Security web 安全的功能
public class MyWebSecurityConfig extends WebSecurityConfigurerAdapter
{
    private final static Logger logger = LoggerFactory.getLogger(MyWebSecurityConfig.class);
    /**
     * 通过 实现UserDetailService 来进行验证
     */
    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private MyAuthenticationSuccessHandler myAuthenticationSuccessHandler;
    @Autowired
    private MyAuthenticationFailureHandler myAuthenticationFailureHandler;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private ValidateCodeFilter validateCodeFilter;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    /**
     * 配置TokenRepository
     * @return
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        // 配置数据源
        jdbcTokenRepository.setDataSource(dataSource);
        //jdbcTokenRepository.setCreateTableOnStartup(true);
        return jdbcTokenRepository;
    }

    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //校验用户
        auth.userDetailsService(myUserDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }
        /**
         * 自定义配置
         */
        @Override
        protected void configure (HttpSecurity http) throws Exception {
            http.authorizeRequests()//配置安全策略
                    .antMatchers("/index","/user/login", "/user/regist", "/qrcode/generateqrcode","/kaptcha","/mq/mqSendTopMsg","/secKill/doseckill","/testquarzt").permitAll()
                    .antMatchers("/css/**", "/js/**", "/images/**").permitAll()
                    .antMatchers("/user/admin/**").hasRole("ADMIN") //以 "/admin/" 开头的URL只能由拥有 "ROLE_ADMIN"角色的用户访问。请注意我们使用 hasRole 方法，没有使用 "ROLE_" 前缀.
                    .anyRequest().authenticated()//其余的所有请求都需要验证
                    .and()                              //使用and()方法相当于XML标签的关闭
                    //关闭验证码过滤器
                    .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class);
            http.formLogin()
                    .loginPage("/user/login")            //拦截后get请求跳转的页面,指定的登录页的路径
                    .loginProcessingUrl("/user/login")   //与登录页面请求的action行为一致，即表示登录操作行为的Url
                    .successHandler(myAuthenticationSuccessHandler)
                    .failureHandler(myAuthenticationFailureHandler)
                    .and()
                    .rememberMe()
                        .rememberMeParameter("remember-me").userDetailsService(myUserDetailsService)
                        .tokenRepository(persistentTokenRepository())
                         // 失效时间
                         .tokenValiditySeconds(3600)
                    .and()
                    .logout().permitAll()
                    .and()
                    //关闭跨站请求防护
                    .csrf().disable();
        }

}
