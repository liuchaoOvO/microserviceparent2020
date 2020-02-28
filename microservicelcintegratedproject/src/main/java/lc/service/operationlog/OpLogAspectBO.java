package lc.service.operationlog;

import lc.dao.opLog.OpLogDao;
import lc.entity.OpLogDto;
import lc.util.OpLog;
import lc.util.RequestUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

/**
 * @author liuchaoOvO on 2019/4/11
 */
@Aspect
@Service
public class OpLogAspectBO {
    private static final String PointcutURL = "@annotation(lc.util.OpLog)";
    private static final String STR_SUCCESS = "[执行成功] ";
    private static final String STR_ERROR = "[执行失败] ";
    private static final String STR_ERRORMSG = " [错误信息] ";
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private OpLogDao opLogDao;

    @Before (value = PointcutURL)
    public void beforeMethod(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("【前置通知】the method 【" + methodName + "】 begins with " + Arrays.asList(joinPoint.getArgs()));
        try {
            String targetName = joinPoint.getTarget().getClass().getName();
            Class<?> targetClass = Class.forName(targetName);
            Method[] methods = targetClass.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    logger.info("==beforeMethod   begin===");
                    logger.info("System:==" + method.getAnnotation(OpLog.class).system());
                    logger.info("Module:==" + method.getAnnotation(OpLog.class).module());
                    logger.info("Menu:==" + method.getAnnotation(OpLog.class).menu());
                    logger.info("Function:==" + method.getAnnotation(OpLog.class).function());
                    logger.info("Content:==" + method.getAnnotation(OpLog.class).content());
                    logger.info("==beforeMethod   end===");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 后置通知：目标方法执行之后执行以下方法体的内容，不管是否发生异常。
     *
     * @param jp
     */
    @After (value = PointcutURL)
    public void afterMethod(JoinPoint jp) {
        System.out.println("【后置通知】this is a afterMethod advice...");
    }

    /**
     * AfterReturning 操作时记录日志
     *
     * @throws ClassNotFoundException
     */
    @AfterReturning (returning = "result", pointcut = PointcutURL)
    public void afterReturningOperationLog(JoinPoint joinPoint, Object result) throws ClassNotFoundException {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("【返回通知】the method 【" + methodName + "】 ends with 【" + result + "】");

        OpLogDto opLogDto = createOpLog(joinPoint, result, null);

        Boolean a = opLogDao.saveOpLogToDB(opLogDto);
        logger.info("日志是否记录成功:==" + a.toString());
    }

    /**
     * AfterThrowing 操作时记录日志
     *
     * @throws ClassNotFoundException
     */
    @AfterThrowing (throwing = "ex", pointcut = PointcutURL)
    public void afterThrowingOperationLog(JoinPoint joinPoint, Throwable ex) throws ClassNotFoundException {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("【异常通知】the method 【" + methodName + "】 occurs exception: " + ex);
    }

    private OpLogDto createOpLog(JoinPoint joinPoint, Object result, Throwable ex) throws ClassNotFoundException {
        OpLogDto logDto = new OpLogDto();
        logDto.setLogId(generateLogId());
        logDto.setRecordTime(new Date());
        String logMsg = "";

        String targetName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] arguments = joinPoint.getArgs();
        Class<?> targetClass = Class.forName(targetName);
        Method[] methods = targetClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                logDto.setSystem(method.getAnnotation(OpLog.class).system());
                logDto.setModule(method.getAnnotation(OpLog.class).module());
                logDto.setMenu(method.getAnnotation(OpLog.class).menu());
                logDto.setFunction(method.getAnnotation(OpLog.class).function());
                String logExp = method.getAnnotation(OpLog.class).content();
                // 计算日志信息表达式
                EvaluationContext evlContext = new StandardEvaluationContext();
                for (int i = 0; i < arguments.length; i++) {
                    /*
                     * jdk1.8+ 可通过 method.getParameters() 读取参数名称 jdk1.6
                     * 只能用参数序号，arg0、arg1...，不能用参数名称
                     */
                    evlContext.setVariable("arg" + i, arguments[i]);
                }
                logMsg = getExpressionValue(logExp, evlContext);
                // 取前端 ip 地址
                Class<?>[] parmeterTypes = method.getParameterTypes();
                for (int i = 0; i < parmeterTypes.length; i++) {
                    if (parmeterTypes[i].getName().equalsIgnoreCase("javax.servlet.http.HttpServletRequest")) {
                        HttpServletRequest request = (HttpServletRequest) arguments[i];
                        logDto.setIpAddress(RequestUtil.getIpAddress(request));
                        logDto.setUrl(request.getServletPath());
                    }
                }
            }
        }
        if (ex == null) {
            logDto.setStatus(1);
            logDto.setMessage(STR_SUCCESS + logMsg);
        } else {
            logDto.setStatus(0);
            logDto.setMessage(STR_ERROR + logMsg + STR_ERRORMSG + ex.getMessage());
        }
        return logDto;
    }

    private String generateLogId() {
        return System.currentTimeMillis() + "-" + UUID.randomUUID().toString();
    }

    /**
     * 计算日志内容表达式的值 <br/>
     *
     * @param logExpression 日志内容表达式
     * @return 操作描述
     * @throws Exception
     */
    private String getExpressionValue(String logExpression, EvaluationContext context) {
        try {
            ExpressionParser logParser = new SpelExpressionParser();
            Expression expression = logParser.parseExpression(logExpression);
            return expression.getValue(context).toString();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
