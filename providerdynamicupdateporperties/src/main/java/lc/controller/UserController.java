package lc.controller;

/**
 * @author liuchaoOvO on 2019/3/15
 */

import lc.entity.SysUser;
import lc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @RequestMapping(value="/add", method=RequestMethod.POST)
    public boolean addUser(@RequestBody SysUser user){
        boolean flag = service.addUser(user);
        return flag;
    }

    @RequestMapping(value="/get/{id}", method=RequestMethod.GET)
    public SysUser getUser(@PathVariable("id") int id){
        SysUser user = service.getUser(id);
        System.out.println("microservice-provider 端口为7001 微服务在响应客户端请求……");
        System.out.println("user : " + user);
        return user;
    }

    @RequestMapping(value="/getUser/list", method=RequestMethod.GET)
    public List<SysUser> getUsers(){
        List<SysUser> users = service.getUsers();
        return users;
    }
}
