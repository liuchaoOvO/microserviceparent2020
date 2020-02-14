package lc.handler;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author: CoderTnT
 * @date: 2018/8/31
 * 自定了权限不足的返回值
 */
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler
{

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setStatus(403);
    }

}
