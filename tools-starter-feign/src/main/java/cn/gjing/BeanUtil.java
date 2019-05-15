package cn.gjing;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Gjing
 **/
@Component
class BeanUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (BeanUtil.applicationContext == null) {
            BeanUtil.applicationContext = applicationContext;
        }
    }

    private static ApplicationContext getApplicationContext() {
        return applicationContext;
    }


    static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }
}
