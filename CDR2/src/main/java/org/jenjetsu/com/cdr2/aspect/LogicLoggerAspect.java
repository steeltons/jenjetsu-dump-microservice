package org.jenjetsu.com.cdr2.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LogicLoggerAspect {

    @Before("execution(* org.jenjetsu.com.cdr2.logic.*.*(..))")
    public void logBefore(JoinPoint point) {
        Signature signature = point.getSignature();
        log.info(String.format("Execute %s %s", signature.getDeclaringTypeName(), signature.getName()));
        byte counter = 0;
        for(Object arg : point.getArgs()) {
            log.info(String.format("Arg[%d] : %s", counter++, arg.toString()));
        }
    }

    @AfterThrowing(value = "execution(* org.jenjetsu.com.cdr2.logic.*.*(..))", throwing = "e")
    public void logError(JoinPoint point, Error e) {
        Signature signature = point.getSignature();
        log.error(String.format("ERROR IN %s %s", signature.getDeclaringTypeName(), signature.getName()));
        log.error(String.format("ERROR MESSAGE : %s", e.getMessage()));
    }
}
