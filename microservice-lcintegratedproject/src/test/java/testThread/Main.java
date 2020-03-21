package testThread;



import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuchaoOvO on 2019/4/17
 */
public class Main
{
    public static void main(String[] args)
    {
        AtomicInteger result= new AtomicInteger(10);
        ThreadConsumer tc = new ThreadConsumer(result);
        ThreadProducer tp = new ThreadProducer(result);
        Thread t1 = new Thread(tp);
        Thread t2 = new Thread(tc);
        t1.start();
        t2.start();
    }
}
