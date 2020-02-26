package lc.controller.security;

import com.github.pagehelper.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lc.entity.SysUser;
import lc.service.security.MySysUserService;
import lc.service.user.UserService;
import lc.util.CustomParamBinding;
import lc.util.OpLog;
import lc.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by CoderTnT on 2018/7/31.
 */
@Controller
@RequestMapping (value = "/user")
@Api ("安全登录api")
public class UserController {
    private final static Logger logger = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private MySysUserService mySysUserService;
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserService userService;

    @ApiOperation (value = "登录请求跳转", notes = "根据springsecurity 配置的config 跳转到具体的登录页面")
    @GetMapping (value = "/login")
    public String login() {
        logger.info("get=====login");
        return "/user/login";
    }

    @OpLog (system = "lc系统", module = "lc模块", menu = "登录菜单", function = "login", content = "'登录操作= --' + #arg0.getRequestURL()")
    @PostMapping (value = "/login")
    public String login(HttpServletRequest request, HttpServletResponse response, Model model) {
        System.out.println("==打开主菜单==》index.  发送的是/user/login 的post请求=");
        return "index";
    }

    @GetMapping (value = "/regist")
    public String registUI() {
        return "/user/regist";
    }

    @OpLog (system = "lc系统", module = "lc模块", menu = "注册菜单", function = "regist", content = "'注册操作= --' + #arg0.getRequestURL()")
    @ApiOperation (value = "注册 请求", notes = "输入用户实体信息  进行用户注册")
    @ApiImplicitParam (name = "SysUser", value = "用户实体", required = true, dataType = "SysUser")
    @PostMapping (value = "/regist")
    public String regist(SysUser user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setStatus(1);
        //判断注册的用户是否存在 若存在则返回到注册页面
        SysUser judgeUser = mySysUserService.findUserByUsername(user.getUsername());
        if (judgeUser != null) {
            return "/user/regist";
        }
        //uuid 设置主键
        user.setId(UUIDUtil.uuid());
        boolean a = mySysUserService.registUser(user);

        if (a) {
            return "/user/login";
        } else return "/user/regist";
    }

    @GetMapping (value = "/admin")
    public String admin(Model model) {
        model.addAttribute("extraInfo", "你是admin");
        return "admin";
    }

    //http://localhost:8888/user/getUserList?pageNum=1&pageSize=3
    @RequestMapping (value = "/getUserList", produces = "text/plain;charset=utf-8")
    public String getUserList(@RequestParam (value = "pageNum") Integer pageNum, @RequestParam (value = "pageSize") Integer pageSize, Model model) {
        Page<SysUser> userList = userService.getUserList(pageNum, pageSize);
        model.addAttribute("userList", userList);
        return "/user/userList";
    }

    @RequestMapping (value = "/userRegist", method = RequestMethod.POST)
    public void userRegist(@CustomParamBinding @RequestBody SysUser sysUser) throws Exception {
        boolean flag = userService.addUser(sysUser);
        if (flag == true) {
            logger.info("注册用户成功》》");
        } else {
            logger.info("注册用户失败》》");
        }
    }
}
