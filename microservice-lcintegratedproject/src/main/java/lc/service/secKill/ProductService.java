package lc.service.secKill;

import lc.entity.GoodsVo;

import java.util.List;

/**
 * @author liuchaoOvO on 2019/5/22
 */
public interface ProductService {

    List<GoodsVo> listGoodsVo();

    boolean reduceStock(GoodsVo goodsVo) throws Exception;

    GoodsVo getGoodsVoByGoodsId(long goodsId);
}
