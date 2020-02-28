package lc.service.impl;

import com.codingapi.txlcn.tc.annotation.DTXPropagation;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import lc.dao.product.ProductDao;
import lc.dto.SeckillGoods;
import lc.entity.GoodsVo;
import lc.service.secKill.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuchaoOvO on 2019/5/22
 */
@Service
public class ProductServiceImpl implements ProductService {
    //乐观锁冲突最大重试次数
    private static final int DEFAULT_MAX_RETRIES = 5;
    @Autowired
    private ProductDao productDao;

    @Override
    public List<GoodsVo> listGoodsVo() {
        return productDao.listGoodsVo();
    }

    /**
     * 减少库存，每次减一
     *
     * @return
     */
    @Override
    @LcnTransaction (propagation = DTXPropagation.SUPPORTS)//分布式事务注解
    public boolean reduceStock(GoodsVo goodsVo) throws Exception {
        int numAttempts = 0;
        int ret = 0;
        SeckillGoods sg = new SeckillGoods();
        sg.setGoodsId(goodsVo.getId());
        sg.setVersion(goodsVo.getVersion());
        try {
            sg.setVersion(productDao.getVersionByGoodsId(goodsVo.getId()));
            ret = productDao.reduceStockByVersion(sg);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
        return ret > 0;
    }

    @Override
    public GoodsVo getGoodsVoByGoodsId(long goodsId) {
        return productDao.getGoodsVoByGoodsId(goodsId);
    }
}
