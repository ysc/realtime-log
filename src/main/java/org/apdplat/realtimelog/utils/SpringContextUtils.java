package org.apdplat.realtimelog.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * Created by ysc on 01/07/2018.
 */
@Service
public class SpringContextUtils implements ApplicationContextAware {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringContextUtils.class);

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        LOGGER.info("设置Spring上下文");
        applicationContext = context;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) throws BeansException {
        try {
            if (applicationContext == null || applicationContext.getBean(name) == null) {
                LOGGER.error("获取不到bean: {}", name);
                return null;
            }
            return (T) applicationContext.getBean(name);
        }catch (Exception e){
            LOGGER.error("获取不到bean: {}", name);
            return null;
        }
    }
}
