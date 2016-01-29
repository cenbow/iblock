package com.iblock.common.advice;

import org.aspectj.lang.JoinPoint;

/**
 * Created by yuqihong on 15/6/9.
 */
public abstract class BaseAdvice {

    public BaseAdvice() {
    }

    protected String getSimpleClassName(JoinPoint joinPoint) {
        return joinPoint.getTarget().getClass().getSimpleName();
    }

    protected String getInvokeId(String methodName) {
        return String.format("%s_%d", methodName, System.nanoTime());
    }

}
