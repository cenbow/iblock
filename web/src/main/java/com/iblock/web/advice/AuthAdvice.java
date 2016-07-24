package com.iblock.web.advice;

import com.iblock.common.advice.Auth;
import com.iblock.web.controller.BaseController;
import com.iblock.web.enums.ResponseStatus;
import com.iblock.web.info.UserInfo;
import com.iblock.web.response.CommonResponse;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Created by baidu on 16/2/1.
 */
@Component
@Aspect
@Log4j
public class AuthAdvice implements Ordered {

    @Around(value = "execution(* com.iblock.web.controller..*.*(..))")
    public Object auth(ProceedingJoinPoint joinPoint) throws Throwable {
        BaseController controller = (BaseController) joinPoint.getTarget();
        UserInfo user = controller.getUserInfo();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Auth auth = methodSignature.getMethod().getAnnotation(Auth.class);
        if (auth != null && (user == null || (StringUtils.isNotBlank(auth.role()) && !auth.role().contains(String
                .valueOf(user.getRole()))))) {
            log.info(String.format("accessor has no auth, interface auth is %s and accessor role is %d", auth.role(),
                    user.getRole()));
            return new CommonResponse<Boolean>(ResponseStatus.NO_AUTH);
        }
        return joinPoint.proceed();
    }

    public int getOrder() {
        return 2;
    }
}
