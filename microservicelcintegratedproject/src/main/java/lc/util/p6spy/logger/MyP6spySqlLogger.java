package lc.util.p6spy.logger;

/**
 * @author liuchaoOvO on 2020/1/6
 * @Classname MyP6spySqlLogger
 * @Description TODO
 */

import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.Slf4JLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyP6spySqlLogger extends Slf4JLogger {
    private Logger log = LoggerFactory.getLogger("p6spy");

    public MyP6spySqlLogger() {
    }

    public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql, String url) {
    }
}

