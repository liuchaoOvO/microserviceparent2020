package testThread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author liuchaoOvO on 2019/4/17
 */
public class ThreadProducer implements Runnable
{
    private AtomicInteger result;

    ThreadProducer(AtomicInteger result)
    {
        this.result = result;
    }

    @Override
    public void run()
    {
        product();
    }
    private synchronized void  product()
    {
        while (true)
        {
            if (result.get()<20)
            {
                try {
                result.incrementAndGet();
                System.out.println(Thread.currentThread().getName() + "生产一件资源，当前资源池有"
                        + result + "个");
                    Thread.sleep(1500);

                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            } else
            {
                try
                {
                    this.notify();
                    this.wait();
                    System.out.println(Thread.currentThread().getName() + "暂停生产");
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}

