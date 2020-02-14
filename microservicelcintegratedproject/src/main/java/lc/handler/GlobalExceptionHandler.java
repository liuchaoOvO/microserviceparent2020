package lc.handler;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CoderTnT on 2018/8/7.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MultipartException.class)
    public String handleError1(MultipartException e, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", e.getCause().getMessage());
        return "redirect:/uploadStatus";
    }
	@ExceptionHandler(RuntimeException.class)
    public String resultError(Model model) {
        Map<String,Object> result=new HashMap<String,Object>();
        result.put("errorCode","404");
        result.put("msg","运行时抛出异常错误，导致系统崩溃了!请查看日志文件处理！");
        model.addAttribute("result",result);
        return "/404";

    }

}