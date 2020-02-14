package lc.service.secKill;

import lc.dto.OrderInfo;
import lc.entity.GoodsVo;
import lc.entity.SecOrder;
import lc.entity.SecProductInfo;
import lc.entity.SysUser;
import lc.exception.SellException;
import org.springframework.stereotype.Service;

/**
 * @author liuchaoOvO on 2019/5/22
 */
public interface  SecKillService
{


        GoodsVo getGoodsVoByGoodsId(long goodsId);

        OrderInfo seckill(SysUser user, GoodsVo goodsVo) throws Exception;

        long getSeckillResult(String id, long goodsId);
}
