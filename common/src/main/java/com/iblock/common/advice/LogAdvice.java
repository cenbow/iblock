package com.iblock.common.advice;

import com.iblock.common.utils.JsonUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 日志记录切面
 * Created by yuqihong on 15/6/9.
 */
@Component
@Aspect
public class LogAdvice extends BaseAdvice {

    public static final String BEFORE_INVOKE_MSG_FORMAT = "invokeId=[%s] start invoking [%s], params=[%s]";
    public static final String AFTER_INVOKE_MSG_FORMAT = "invokeId=[%s] complete invoking [%s], "
            + "result=[%s], elapsed=[%d] ms";
    private static ThreadLocal<Long> startTime = new ThreadLocal() {
        protected Long initialValue() {
            return System.currentTimeMillis();
        }
    };

    /**
     * 记录方法调用的起始日志
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around(value = "execution(* com.iblock.web.controller..*.*(..)) "
                + "|| execution(* com.iblock.service..*.*(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        startTime.set(System.currentTimeMillis());
        String simpleClassName = this.getSimpleClassName(joinPoint);
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        SimplifyLog simplifyLog =  methodSignature.getMethod().getAnnotation(SimplifyLog.class);
        String methodName = methodSignature.getName();
        String invokeId = this.getInvokeId(methodName);
        Logger logger = Logger.getLogger(simpleClassName);
        try {
            this.logBeforeInvokeMethod(logger, invokeId, methodName, joinPoint.getArgs(), simplifyLog != null
                    && simplifyLog.simplifyParam());
        } catch (Exception e) {
            logger.warn(String.format("args not support log writter! invokeId : [%s], method: [%s]",
                    invokeId, methodName));
        }
        Object result = joinPoint.proceed();
        this.logAfterInvokeMethod(logger, invokeId, methodName, result, simplifyLog != null && simplifyLog
                .simplifyReturn());
        return result;
    }

    private void logAfterInvokeMethod(Logger logger, String invokeId, String method, Object result, boolean simplify)
            throws IOException {
        long elapsed = System.currentTimeMillis() - startTime.get();
        String s = "";
        if (result != null) {
            try {
                s = JsonUtils.toStr(result);
            } catch (IOException e) {
                s = result.toString();
            }
        }
        logger.info(String.format(AFTER_INVOKE_MSG_FORMAT, invokeId, method, simplify && s.length() > 200 ? s
                        .substring(0, 200) : s, elapsed));
    }

    private void logBeforeInvokeMethod(Logger logger, String invokeId,
                                       String methodName, Object[] args, boolean simplify) throws IOException {
        StringBuilder sb = new StringBuilder();
        if (!ArrayUtils.isEmpty(args)) {
            for (int i = 0; i < args.length; ++i) {
                Object arg = args[i];
                try {
                    sb.append(JsonUtils.toStr(arg));
                } catch (IOException e) {
                    sb.append(arg.toString());
                }
                if (i + 1 != args.length) {
                    sb.append(",");
                }
            }
        }
        logger.info(String.format(BEFORE_INVOKE_MSG_FORMAT, invokeId, methodName, simplify && sb.length() > 200 ? sb
                .substring(0, 200) : sb.toString()));
    }

    protected String getSimpleClassName(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getSimpleName();
    }

}