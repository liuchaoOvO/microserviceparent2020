package lc.controller;

import lc.entity.SysUser;
import lc.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author liuchaoOvO on 2019/3/15
 */
@RestController
public class UserConsumerController {

    @Autowired
    private ConsumerService service;

    @RequestMapping (value = "/consumer/add")
    public boolean addUser(SysUser user) {
        Boolean flag = service.add(user);
        return flag;
    }

    @RequestMapping (value = "/consumer/get/{id}")
    public SysUser get(@PathVariable ("id") String id) {
        SysUser user = service.get(id);
        return user;
    }

    @RequestMapping (value = "/consumer/list")
    public List<SysUser> getList() {
        List list = service.getAll();
        return list;
    }

}
