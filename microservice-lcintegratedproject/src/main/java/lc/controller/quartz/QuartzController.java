package lc.controller.quartz;


import lc.dto.QuartzTaskRecordsVo;
import lc.entity.QuartzTaskErrors;
import lc.entity.QuartzTaskInformations;
import lc.service.quartz.QuartzService;
import lc.util.ResultEnum;
import lc.util.ResultUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @ClassName QuartzController
 * @Description quartz controller主要逻辑
 * @Author  liuchaoOvO
 * @Date    2019/1/3
 * Version   1.0
 */
@Controller
@RequestMapping("/quartz")
public class QuartzController {

    private static final Logger logger = LoggerFactory.getLogger(QuartzController.class);

    @Autowired
    private QuartzService quartzService;

    @RequestMapping(value = "/add/taskpage", method = RequestMethod.GET)
    public String addTaskpage() {
        return "/task/addtask";
    }

    @ResponseBody
    @RequestMapping(value = "/add/task", method = RequestMethod.POST)
    public String addTask(QuartzTaskInformations taskInformations) {
        try {
            String result = quartzService.addTask(taskInformations);
            return result;
        } catch (Exception e) {
            logger.error("/add/task exception={}", e);
            return ResultUtil.fail();
        }
    }

    @RequestMapping(value = "/edit/taskpage", method = RequestMethod.GET)
    public String editTaskpage(Model model, String id) {
        QuartzTaskInformations taskInformation = quartzService.getTaskById(id);
        model.addAttribute("taskInformation", taskInformation);
        return "/task/updatetask";
    }

    @ResponseBody
    @RequestMapping(value = "/edit/task", method = RequestMethod.POST)
    public String editTask(QuartzTaskInformations taskInformations) {
        try {
            String result = quartzService.updateTask(taskInformations);
            return result;
        } catch (Exception e) {
            logger.error("/edit/task exception={}", e);
            return ResultUtil.fail();
        }
    }

    /**
     * 启动 或者 暂定定时任务
     *
     * @param taskNo
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list/optionjob", method = RequestMethod.GET)
    public String optionJob(String taskNo) {
        logger.info("");
        if (StringUtils.isEmpty(taskNo)) {
            return ResultUtil.success(ResultEnum.PARAM_EMPTY.getCode(), ResultEnum.PARAM_EMPTY.getMessage());
        }
        try {
            return quartzService.startJob(taskNo);
        } catch (Exception e) {
            logger.error("/list/optionjob exception={}", e);
            return ResultUtil.fail();
        }
    }

    /**
     * 定时任务执行情况
     *
     * @param taskNo
     * @param model
     * @return
     */
    @RequestMapping(value = "/taskrecords", method = RequestMethod.GET)
    public String taskRecordsPage(@RequestParam(value = "taskno", required = false) String taskNo, Model model) {
        logger.info("");
        try {
            if (StringUtils.isEmpty(taskNo)) {
                return "redirect:/";
            }
            List<QuartzTaskRecordsVo> quartzTaskRecords = quartzService.taskRecords(taskNo);
            model.addAttribute("size", quartzTaskRecords.size());
            model.addAttribute("quartzTaskRecords", quartzTaskRecords);
        } catch (Exception e) {
            logger.error("");
            return "redirect:/";
        }
        return "/task/taskrecords";
    }

    /**
     * 立即运行一次定时任务
     *
     * @param taskNo
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/runtask/rightnow", method = RequestMethod.GET)
    public String runTaskRightNow(@RequestParam(value = "taskNo", required = false) String taskNo, Model model) {
        logger.info("");
        try {
            if (StringUtils.isEmpty(taskNo)) {
                return ResultUtil.success(ResultEnum.PARAM_EMPTY.getCode(), ResultEnum.PARAM_EMPTY.getMessage());
            }
            return quartzService.runTaskRightNow(taskNo);
        } catch (Exception e) {
            logger.error("");
            return ResultUtil.success(ResultEnum.FAIL.getCode(), ResultEnum.FAIL.getMessage());
        }
    }

    /**
     * 定时任务失败详情
     *
     * @param recordId
     * @param model
     * @return
     */
    @RequestMapping(value = "/task/errors", method = RequestMethod.GET)
    public String detailTaskErrors(@RequestParam(value = "recordid", required = false) String recordId, Model model) {
        logger.info("");
        try {
            if (StringUtils.isEmpty(recordId)) {
                return ResultUtil.success(ResultEnum.PARAM_EMPTY.getCode(), ResultEnum.PARAM_EMPTY.getMessage());
            }
            QuartzTaskErrors taskErrors = quartzService.detailTaskErrors(recordId);
            model.addAttribute("taskErrors", taskErrors);
            return "/task/taskerrors";
        } catch (Exception e) {
            logger.error("");
            return "redirect:/";
        }
    }

    /**
     * 列表查询所有定时任务
     *
     * @return
     */

    @RequestMapping(value = "/task/getTaskList", method = RequestMethod.GET)
    public String getTaskList(@RequestParam(value="currentPage")String currentPage, Model model) {
        String taskNo=null;
        List<QuartzTaskInformations> taskList= quartzService.getTaskList(taskNo,currentPage);
        //查询记录总数量
        int size=quartzService.getTaskCountNum();
        model.addAttribute("taskList", taskList);
        model.addAttribute("size", size);
        return "/task/taskList";
    }


}
