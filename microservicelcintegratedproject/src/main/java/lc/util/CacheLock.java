package lc.util;

import java.lang.annotation.*;

/**
 * @author liuchaoOvO on 2019/5/22
 */
@Target (ElementType.METHOD)
@Retention (RetentionPolicy.RUNTIME)
@Documented
public @interface CacheLock {
    String lockedPrefix() default "";//redis 锁key的前缀

    long timeOut() default 2000;//锁时间

    long expireTime() default 100000;//key在redis里存在的时间，1000S
}