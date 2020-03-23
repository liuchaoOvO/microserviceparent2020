package lc.controller.updateSqlScript;


import lc.service.updateSqlScript.DbUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author liuchaoOvO on 2019/4/1
 */

@RestController
public class DbUpdateController
{
    @Autowired
    private DbUpdateService dbUpdateService;

    @RequestMapping(value="/dbUpdate", method= RequestMethod.GET)
    public boolean dbUpdate() throws Exception{
        dbUpdateService.doSqlScript();
        return true;
    }
}

