package com.nameof.web.aop.log;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggerAdvice {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Before("within(com.nameof..*) && @annotation(autoLog)")
	public void addBeforeLogger(JoinPoint joinPoint, AutoLog autoLog) {
		logger.info("执行 " + autoLog.description() + " 开始");
		logger.info(joinPoint.getSignature().toString());
		logger.info(parseParames(joinPoint.getArgs()));
	}
	
	@AfterReturning("within(com.nameof..*) && @annotation(autoLog)")
	public void addAfterReturningLogger(JoinPoint joinPoint, AutoLog autoLog) {
		logger.info("执行 " + autoLog.description() + " 结束");
	}
	
	@AfterThrowing(pointcut = "within(com.nameof..*) && @annotation(autoLog)", throwing = "ex")
	public void addAfterThrowingLogger(JoinPoint joinPoint, AutoLog autoLog, Exception ex) {
		logger.error("执行 " + autoLog.description() + " 异常", ex);
	}

	private String parseParames(Object[] parames) {
		if (null == parames || parames.length <= 0) {
			return "";
		}
		StringBuffer param = new StringBuffer("传入参数[{}] ");
		for (Object obj : parames) {
			param.append(obj == null ? "''  " : ToStringBuilder.reflectionToString(obj)).append("  ");
		}
		return param.toString();
	}
	
}
