package org.zhu.config;

import cn.hutool.core.date.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Aspect
public class ServiceLogAspect {

    /**
     * 拦截所有service包下的所有方法
     * @Description: AOP 环绕切面
     *               org.zhu.service.impl 指定的包名，要切的class类的所在包
     *               * 匹配当前包以及子包中的类
     *               . 无意义
     *               * 匹配任意方法名
     *               (..) 方法的参数，匹配任意参数
     */
    @Around("execution(* org.zhu.service.impl..*.*(..))")
    public Object recordTimesLog(ProceedingJoinPoint joinPoint) throws Throwable {
        // long begin = System.currentTimeMillis();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Object proceed = joinPoint.proceed();

        String point = joinPoint.getTarget().getClass().getName()
                + "."
                + joinPoint.getSignature().getName();

        // long end = System.currentTimeMillis();
        stopWatch.stop();

        // long takeTime = end - begin;

        long takeTime = stopWatch.getTotalTimeMillis();

        if(takeTime > 3000){
            log.error("{} 耗时偏长 {} 毫秒", point, takeTime);
        } else if (takeTime > 2000){
            log.warn("{} 耗时中等 {} 毫秒", point, takeTime);
        } else {
            log.info("{} 耗时 {} 毫秒", point, takeTime);
        }

        return proceed;
    }
}
