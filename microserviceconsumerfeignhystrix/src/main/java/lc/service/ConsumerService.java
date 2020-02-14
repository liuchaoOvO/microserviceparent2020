package lc.service;

/**
 * @author liuchaoOvO on 2019/3/17
 */

import lc.entity.SysUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/*以后调用microservicecloud-provider微服务中的方法，只需要调用下面对应的接口既可以了*/
@FeignClient(value="microservicecloud-provider",fallbackFactory=ConsumerServiceFallbackFactory.class)
public interface ConsumerService {

    /*调用接口中的get方法，即可以向microservicecloud-provider微服务发送/get/{id}请求*/
    @RequestMapping(value="/get/{id}", method=RequestMethod.GET)
    public SysUser get(@PathVariable("id") String id);

    @RequestMapping(value="/add", method=RequestMethod.POST)
    public boolean add(SysUser user);

    @RequestMapping(value="/getUser/list", method=RequestMethod.GET)
    public List<SysUser> getAll();
}
