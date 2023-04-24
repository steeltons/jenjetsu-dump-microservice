package org.jenjetsu.com.brt.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.jenjetsu.com.core.dto.AbonentDto;
import org.jenjetsu.com.core.service.JwtParser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * <h2>Abonent phone access aspect</h2>
 * Aspect class that protect phone numbers from unscrupulous abonent.
 * It doesn't work on manager. He is god.
 */
@Component
@Aspect
public class AbonentPhoneAccessAspect {

    private final Pattern simpleNumberPattern = Pattern.compile("[0-9]{11}");

    /**
     * <h2>Check access to create report</h2>
     * Method which protects against unauthorized access to phone calls
     * from a third party abonent
     * @param joinPoint
     * @throws AccessDeniedException - if phone numbers are not equals
     */
    @Before("execution(* org.jenjetsu.com.brt.rest.AbonentRestController.getReports(..))")
    public void checkAccessToCreateReport(JoinPoint joinPoint) {
        String name = getUsernameFromAuthentication();
        if(!simpleNumberPattern.matcher(name).find())
            return;
        String inputPhone = joinPoint.getArgs()[0].toString();
        if(!inputPhone.equals(name)) {
            throw new AccessDeniedException(String.format("%s tried to get phone calls of %s . Access denied", name, inputPhone));
        }
    }

    /**
     * <h2>Check access to add money</h2>
     * Method which protects against unauthorized to add money
     * from a third party abonent
     * @param joinPoint
     * @throws AccessDeniedException - if phone numbers are not equals
     */
    @Before("execution(* org.jenjetsu.com.brt.rest.AbonentRestController.pay(..))")
    public void checkAccessToAddMoney(JoinPoint joinPoint) {
        String name = getUsernameFromAuthentication();
        if(!simpleNumberPattern.matcher(name).find())
            return;
        String inputPhone = ((AbonentDto) joinPoint.getArgs()[0]).numberPhone().toString();
        if(!inputPhone.equals(name)) {
            throw new AccessDeniedException(String.format("%s tried to add balance to %s . Access denied", name, inputPhone));
        }
    }

    private String getUsernameFromAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
