package lc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

/**
 * @author liuchaoOvO on 2018/7/31.
 * @description   WebMVC 配置类
 */
@Configuration
public class MyWebMvcConfig extends WebMvcConfigurerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(MyWebMvcConfig.class);
    @Autowired
    SysUserArgumentResolver sysUserArgumentResolver;

    /**
     * SpringMVC框架回调addArgumentResolvers，然后给Controller的参数赋值
     *
     * @param argumentResolvers
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        logger.debug("MyWebMvcConfig.addArgumentResolvers step into...");
        argumentResolvers.add(sysUserArgumentResolver);
        super.addArgumentResolvers(argumentResolvers);
    }
}
