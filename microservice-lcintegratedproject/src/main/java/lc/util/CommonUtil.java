package lc.util;

import lc.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

/**
 * @ClassName CommonUtil
 * @Description 通用工具类
 * @Author liuchaoOvO
 * @Date 2019/1/8
 * Version  1.0
 */
public class CommonUtil {

    public static volatile ThreadLocal<Map<String, SysUser>> threadLocal = new ThreadLocal();
    @Autowired
    RedisUtil redisUtil;

    /**
     * 获取具体的异常信息
     *
     * @param ex
     * @return
     */
    public static String getExceptionDetail(Exception ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            ex.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }


}
