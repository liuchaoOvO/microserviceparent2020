package lc.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.IOException;

/**
 * @author liuchaoOvO on 2019/5/23
 * 消息体
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class SeckillMessage {

    private SysUser user;
    private long goodsId;

    public SeckillMessage() {

    }

    //增加一个构造函数来手动赋值
    public SeckillMessage(String json) throws IOException {
        SeckillMessage param = new ObjectMapper().readValue(json, SeckillMessage.class);
        this.user = param.getUser();
        this.goodsId = param.getGoodsId();
    }

    public SysUser getUser() {
        return user;
    }

    public void setUser(SysUser user) {
        this.user = user;
    }

    public long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(long goodsId) {
        this.goodsId = goodsId;
    }
}


