package lc.service.secKill;

import lc.dto.OrderInfo;
import lc.entity.GoodsVo;
import lc.entity.SysUser;

/**
 * @author liuchaoOvO on 2019/5/22
 */
public interface SecKillService {


    GoodsVo getGoodsVoByGoodsId(long goodsId);

    OrderInfo seckill(SysUser user, GoodsVo goodsVo) throws Exception;

    long getSeckillResult(String id, long goodsId);
}
