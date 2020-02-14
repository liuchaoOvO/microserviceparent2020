package lc.dao.order;

import lc.dto.OrderInfo;
import lc.dto.SeckillOrder;
import lc.entity.GoodsVo;
import lc.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectKey;

/**
 * @author liuchaoOvO on 2019/5/23
 */
@Mapper
public interface OrderDao
{
    @Insert("insert into sk_order_info(user_id, goods_id, goods_name, goods_count, goods_price, order_channel, status, create_date)values("
            + "#{userId}, #{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate} )")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = long.class, before = false, statement = "select last_insert_id()")
    void insert(OrderInfo orderInfo);
    @Insert("insert into sk_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    void insertSeckillOrder(SeckillOrder seckillOrder);
}
