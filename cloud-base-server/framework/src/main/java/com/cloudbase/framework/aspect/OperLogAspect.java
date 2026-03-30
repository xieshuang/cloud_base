package com.cloudbase.framework.aspect;

import cn.hutool.extra.servlet.ServletUtil;
import com.cloudbase.common.log.annotation.OperLog;
import com.cloudbase.common.log.entity.SysLog;
import com.cloudbase.common.security.context.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class OperLogAspect {

    @Pointcut("@annotation(com.cloudbase.common.log.annotation.OperLog)")
    public void operLogPointcut() {
    }

    @AfterReturning(pointcut = "operLogPointcut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        handleLog(joinPoint, null, null);
    }

    @AfterThrowing(pointcut = "operLogPointcut()", throwing = "exception")
    public void doAfterThrowing(JoinPoint joinPoint, Exception exception) {
        handleLog(joinPoint, null, exception);
    }

    private void handleLog(JoinPoint joinPoint, Object result, Exception exception) {
        SysLog sysLog = new SysLog();
        sysLog.setCreateTime(LocalDateTime.now());

        if (SecurityContext.getUser() != null) {
            sysLog.setUserId(SecurityContext.getUserId());
            sysLog.setUsername(SecurityContext.getUsername());
        }

        try {
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperLog operLog = method.getAnnotation(OperLog.class);
            if (operLog != null) {
                sysLog.setModule(operLog.module());
                sysLog.setContent(operLog.content());
                sysLog.setLogType(operLog.logType());
            }

            sysLog.setMethod(joinPoint.getTarget().getClass().getName() + "." + method.getName());
            sysLog.setRequestMethod(getRequestMethod());
            sysLog.setRequestUrl(getRequestUrl());
            sysLog.setRequestParams(getRequestParams(joinPoint));

            if (exception != null) {
                sysLog.setStatus(1);
                sysLog.setErrorMsg(exception.getMessage());
            } else {
                sysLog.setStatus(0);
            }

            HttpServletRequest request = getRequest();
            if (request != null) {
                sysLog.setIp(ServletUtil.getClientIP(request));
                sysLog.setLocation("");
                sysLog.setUserAgent(request.getHeader("User-Agent"));
                Object startTimeAttr = request.getAttribute("startTime");
                if (startTimeAttr != null) {
                    long startTime = (long) startTimeAttr;
                    sysLog.setExecutionTime(System.currentTimeMillis() - startTime);
                }
            }

            log.info("操作日志: module={}, content={}, username={}, method={}",
                    sysLog.getModule(), sysLog.getContent(), sysLog.getUsername(), sysLog.getMethod());
        } catch (Exception e) {
            log.error("记录操作日志异常", e);
        }
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String getRequestMethod() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getMethod() : "";
    }

    private String getRequestUrl() {
        HttpServletRequest request = getRequest();
        return request != null ? request.getRequestURI() : "";
    }

    private String getRequestParams(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        if (paramNames != null && args != null && paramNames.length == args.length) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < paramNames.length; i++) {
                if (i > 0) {
                    sb.append("&");
                }
                sb.append(paramNames[i]).append("=").append(args[i]);
            }
            return sb.toString();
        }
        return "";
    }
}
