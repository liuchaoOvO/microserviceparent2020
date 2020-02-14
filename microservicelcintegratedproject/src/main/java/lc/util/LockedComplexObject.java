package lc.util;

import java.lang.annotation.*;

/**
 * @author liuchaoOvO on 2019/5/22
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LockedComplexObject {
    String field() default "";//含有成员变量的复杂对象中需要加锁的成员变量，如一个商品对象的商品ID

}
