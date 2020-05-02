package cn.gjing.tools.excel.driven;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gjing
 **/
@Configuration
class ExcelDrivenConfiguration implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        RequestMappingHandlerAdapter handlerAdapter = this.applicationContext.getBean(RequestMappingHandlerAdapter.class);
        List<HandlerMethodReturnValueHandler> returnValueHandlers = handlerAdapter.getReturnValueHandlers();
        List<HandlerMethodReturnValueHandler> valueHandlers = new ArrayList<>();
        valueHandlers.add(new ExcelDrivenWriteHandler());
        valueHandlers.add(new ExcelDrivenReadHandler());
        if (returnValueHandlers != null) {
            valueHandlers.addAll(returnValueHandlers);
        }
        handlerAdapter.setReturnValueHandlers(valueHandlers);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
