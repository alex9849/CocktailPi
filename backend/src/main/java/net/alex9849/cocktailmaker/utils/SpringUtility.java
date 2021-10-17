package net.alex9849.cocktailmaker.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringUtility implements ApplicationContextAware {

    @Autowired
    private static ApplicationContext applicationContext;

    public void setApplicationContext(final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /*
        Get a class bean from the application context
     */
    public static <T> T getBean(final Class clazz) {
        return (T) applicationContext.getBean(clazz);
    }

    /*
        Return the application context if necessary for anything else
     */
    public static ApplicationContext getContext() {
        return applicationContext;
    }

}
