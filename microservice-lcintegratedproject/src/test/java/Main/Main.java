package Main;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * @author liuchaoOvO on 2019/4/13
 */
public class Main {
    public static void main() {

        //new HashMap<String,Object>();
        new ThreadLocal();
        //AbstractApplicationContext;
        new ClassPathXmlApplicationContext();

        new ConcurrentHashMap<>();

        new CopyOnWriteArrayList<>();
//      new AbstractQueuedSynchronizer();
    }
}

class TEST {
    public static void main(String[] args) {
        //线程池
        ExecutorService exec = Executors.newCachedThreadPool();
        //速率是每秒只有3个许可
        final RateLimiter rateLimiter = RateLimiter.create(3.0);

        for (int i = 0; i < 20; i++) {
            final int no = i;
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    try {
                        //获取许可
                        if (rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
                            System.out.println("Accessing: " + no + ",time:"
                                    + new SimpleDateFormat("yy-MM-dd HH:mm:ss").format(new Date()));
                        } else {
                            System.out.println("---aaa----");
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            };
            //执行线程
            exec.execute(runnable);
        }
        //退出线程池
        exec.shutdown();
    }
}


