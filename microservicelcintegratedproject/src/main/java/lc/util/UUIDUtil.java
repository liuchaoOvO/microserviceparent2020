package lc.util;

import java.util.Random;
import java.util.UUID;

/**
 * @author liuchaoOvO on 2019/5/22
 */
public class UUIDUtil
{
    public static   synchronized String   getUniqueKey(){
        Random random = new Random();
        Integer num = random.nextInt(100000);
        return  num.toString()+System.currentTimeMillis();
    }
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
