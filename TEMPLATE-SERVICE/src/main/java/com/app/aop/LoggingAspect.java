package com.app.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private final Logger log =
            LoggerFactory.getLogger(LoggingAspect.class);

    // Matches controller/controllers/service packages
    @Pointcut(
            "within(com.app.controller..*) || " +
                    "within(com.app.controller..*) || " +
                    "within(com.app.service..*)"
    )
    public void applicationPackagePointcut() {
    }

    @Before("applicationPackagePointcut()")
    public void logBefore(JoinPoint joinPoint) {

        log.info(
                "Enter: {}.{}() with argument[s] = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                Arrays.toString(joinPoint.getArgs())
        );
    }

    @AfterReturning(
            pointcut = "applicationPackagePointcut()",
            returning = "result"
    )
    public void logAfterReturning(
            JoinPoint joinPoint,
            Object result
    ) {

        log.info(
                "Exit: {}.{}() with result = {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                result
        );
    }

    @AfterThrowing(
            pointcut = "applicationPackagePointcut()",
            throwing = "e"
    )
    public void logAfterThrowing(
            JoinPoint joinPoint,
            Throwable e
    ) {

        log.error(
                "Exception in {}.{}() with cause = '{}' and message = '{}'",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL",
                e.getMessage()
        );
    }

    @Around("applicationPackagePointcut()")
    public Object logAround(
            ProceedingJoinPoint joinPoint
    ) throws Throwable {

        long start = System.currentTimeMillis();

        try {

            Object result = joinPoint.proceed();

            long elapsedTime =
                    System.currentTimeMillis() - start;

            log.info(
                    "Method {}.{}() execution time: {} ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    elapsedTime
            );

            return result;

        } catch (Throwable e) {

            log.error(
                    "Error in Around advice for {}.{}(): {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    e.getMessage()
            );

            throw e;
        }
    }
}