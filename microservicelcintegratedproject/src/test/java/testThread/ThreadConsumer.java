package testThread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuchaoOvO on 2019/4/17
 */
public class ThreadConsumer implements Runnable
{
    private AtomicInteger result;

    ThreadConsumer(AtomicInteger result)
    {
        this.result = result;
    }

    @Override
    public void run()
    {
        consumer();
    }
    private synchronized void consumer()
    {
        while (true)
        {
            if (0<result.get())
            {
                try
                {
                    result.decrementAndGet();
                    System.out.println("消费者" + Thread.currentThread().getName() +
                            "消耗一件资源，" + "当前线程池有" + result + "个");
                    Thread.sleep(1000);

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
             if (result.get()==0)
            {
                try
                {
                    System.out.println("停止消费。");
                    this.wait();
                    this.notify();
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
