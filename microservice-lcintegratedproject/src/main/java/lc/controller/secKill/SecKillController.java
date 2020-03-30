package lc.controller.secKill;

import com.google.common.hash.BloomFilter;
import com.google.common.util.concurrent.RateLimiter;
import lc.entity.GoodsVo;
import lc.entity.SeckillMessage;
import lc.entity.Status;
import lc.entity.SysUser;
import lc.init.InitLogic;
import lc.mqproducer.MsgProducer;
import lc.service.secKill.ProductService;
import lc.service.secKill.SecKillService;
import lc.util.CustomParamBinding;
import lc.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * @author liuchaoOvO on 2019/4/16
 */
@Controller
@RequestMapping (value = "/secKill")
public class SecKillController {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MsgProducer msgProducer;
    @Autowired
    private ProductService productService;
    @Autowired
    private SecKillService secKillService;
    //基于令牌桶算法的限流实现类
    RateLimiter rateLimiter = RateLimiter.create(10);
    //做标记，判断该商品是否被处理过了
    private HashMap<Long, Boolean> localOverMap = new HashMap<Long, Boolean>();
    //基于bloomFilter的防止缓存击穿策略
    BloomFilter bloomFilter = InitLogic.bloomFilter;

    @PostMapping ("/doseckill")
    public String doseckill(Model model, @CustomParamBinding SysUser sysUser, @RequestParam (value = "goodsId", defaultValue = "1") long goodsId) {

        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
            return "500104, 访问高峰期，请稍等！";
        }
        if (sysUser == null) {
            return "500210, Session不存在或者已经失效";
        }
        if (!bloomFilter.mightContain(sysUser.getId())) {
            return "500301, 用户在bloomFilter中不存在";
        }
        //内存标记，减少redis访问
        boolean over = false;
        if (over) {
            return "500500, 商品已经秒杀完毕";
        }
        //预减库存
        long stock = redisUtil.decre("gs" + "" + goodsId, " ");
        if (stock < 0) {
            afterPropertiesSet();
            long stock2 = redisUtil.decre("gs" + "" + goodsId, " ");
            if (stock2 < 0) {
                localOverMap.put(goodsId, true);
                return "500500, 商品已经秒杀完毕";
            }
        }
        //判断重复秒杀
        Object object = redisUtil.get("" + sysUser.getId() + "_" + goodsId);
        if (object != null) {
            return "500501, 不能重复秒杀";
        }
        //入队
        SeckillMessage message = new SeckillMessage();
        message.setUser(sysUser);
        message.setGoodsId(goodsId);
        String sendMsg = msgProducer.sendSecKillMsg(message);
        Status status = new Status<String>();
        status.setCode(0);
        status.setMsg(sendMsg);
        model.addAttribute(status);
        model.addAttribute("goodsId", goodsId);
        model.addAttribute("userId", sysUser.getId());
        return "index";
    }


    /**
     * 系统初始化,将商品信息加载到redis和本地内存
     */
    public void afterPropertiesSet() {
        List<GoodsVo> goodsVoList = productService.listGoodsVo();
        if (goodsVoList == null) {
            return;
        }
        for (GoodsVo goods : goodsVoList) {
            redisUtil.set("gs" + "" + goods.getId(), goods.getStock_count());
            //初始化商品都是没有处理过的
            localOverMap.put(goods.getId(), false);
        }
    }

    /**
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping (value = "/result", method = RequestMethod.GET)
    public String seckillResult(Model model, @CustomParamBinding SysUser sysUser,
                                @RequestParam ("goodsId") long goodsId) {
        Status status = new Status<String>();
        if (sysUser == null || sysUser.getId() == null) {
            status.setCode(1);
            model.addAttribute(status);
            return "index";
        }
        long resultNum = secKillService.getSeckillResult(sysUser.getId(), goodsId);
        status.setCode(resultNum);
        model.addAttribute(status);
        return "index";
    }
}
