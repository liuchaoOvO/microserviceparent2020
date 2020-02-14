package lc.controller.MultiResolverType;

import lc.entity.Account;
import lc.util.CustomParamBinding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author liuchaoOvO on 2019/4/15
 */
@Controller
@RequestMapping("/")
public class MultiResolverTypeContrller
{
    private static final Logger logger = LoggerFactory.getLogger(MultiResolverTypeContrller.class);
    @PostMapping("/testMultiResolver")
    @ResponseBody
    public String testMultiResolver(@CustomParamBinding  Account account)
    {
        String result="result:=="+account.getAccount_Type();
        return result;
    }
    @RequestMapping("/testRb")
    @ResponseBody
    public Account testRb(@RequestBody Account e) {
        return e;
    }

    @RequestMapping("/testCustomObj")
    @ResponseBody
    public Account testCustomObj(Account e) {
        return e;
    }

    @RequestMapping("/testCustomObjWithRp")
    @ResponseBody
    public Account testCustomObjWithRp(@RequestParam Account e) {
        return e;
    }


}
