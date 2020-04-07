package lc.init;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import lc.entity.SysUser;
import lc.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

/**
 * @author liuchaoOvO on 2020/3/30
 * @Description 启动服务时 把用户数据放到bloomFilter中 秒杀操作时需要该数据 防止缓存击穿
 */
@Slf4j
@Component
public class InitLogic implements InitializingBean {
    @Autowired
    private UserService userService;
    private static int expectedInsertions = 10000; //待检测元素的个数
    private static double fpp = 0.03; //误判率(desired false positive probability)
    public static BloomFilter<CharSequence> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.forName("UTF-8")), expectedInsertions, fpp);


    @Override
    public void afterPropertiesSet() throws Exception {
        log.debug("InitLogic#afterPropertiesSet--往bloomFilter中存入用户信息--");
        //1、查用户
        List<SysUser> userList = userService.getUsers();
        //2、放入bloomFilter
        if (Optional.of(userList).isPresent()) {
            for (SysUser sysUser : userList) {
                bloomFilter.put(sysUser.getId());
            }
        }
    }
}
