package lc.util;

import java.lang.annotation.*;

/**
 * @author liuchaoOvO on 2019/4/11
 */
@Target({ ElementType.PARAMETER, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpLog {

    /**
     * 系统
     */
    String system() default "";

    /**
     * 模块
     */
    String module() default "";

    /**
     * 菜单
     */
    String menu() default "";

    /**
     * 功能
     */
    String function() default "";

    /**
     * 日志内容
     */
    String content() default "";

}
