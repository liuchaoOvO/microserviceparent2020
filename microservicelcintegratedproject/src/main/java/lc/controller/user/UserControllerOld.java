package lc.controller.user;

import com.github.pagehelper.Page;
import lc.entity.SysUser;
import lc.service.user.UserService;
import lc.util.CustomParamBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * @author liuchaoOvO on 2019/4/16
 */
@Controller
@RequestMapping(value = "/user")
public class UserControllerOld
{
    private static final Logger logger = LoggerFactory.getLogger(UserControllerOld.class);

    @Autowired
    private UserService userService;


    @RequestMapping(value="/userRegist", method= RequestMethod.POST)
    public void userRegist(@CustomParamBinding @RequestBody SysUser sysUser) throws Exception{
    boolean flag=userService.addUser(sysUser);
    if(flag==true){
        logger.info("注册用户成功》》");
    }else{
        logger.info("注册用户失败》》");
    }
    }
    //http://localhost:8888/user/getUserList?pageNum=1&pageSize=3
    @RequestMapping(value = "/getUserList", produces = "text/plain;charset=utf-8")
    public String getUserList(@RequestParam(value="pageNum") Integer pageNum, @RequestParam(value="pageSize")Integer pageSize, Model model)
    {
        Page<SysUser> userList = userService.getUserList(pageNum, pageSize);
        model.addAttribute("userList", userList);
        return "/user/userList";
    }
}
