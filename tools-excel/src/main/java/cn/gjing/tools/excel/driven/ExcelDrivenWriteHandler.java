package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.ExcelFactory;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.write.resolver.ExcelWriter;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;

/**
 * Excel annotation-driven write handler
 *
 * @author Gjing
 **/
class ExcelDrivenWriteHandler implements HandlerMethodReturnValueHandler {
    @Override
    public boolean supportsReturnType(MethodParameter methodParameter) {
        return methodParameter.hasMethodAnnotation(ExcelWrite.class);
    }

    @Override
    public void handleReturnValue(Object o, @NonNull MethodParameter methodParameter, @NonNull ModelAndViewContainer modelAndViewContainer,
                                  @NonNull NativeWebRequest nativeWebRequest) throws Exception {
        ExcelWrite writerAnno = methodParameter.getMethodAnnotation(ExcelWrite.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        ExcelWriter writer = ExcelFactory.createWriter("".equals(writerAnno.value()) ? null : writerAnno.value(), writerAnno.mapping(), response, writerAnno.initDefaultStyle(), writerAnno.ignores());
        modelAndViewContainer.setRequestHandled(true);
        if (writerAnno.multiHead()) {
            writer.enableMultiHead();
        }
        if (writerAnno.needValid()) {
            writer.enableValid();
        }
        if (o == null) {
            writer.write(null, writerAnno.sheet(), writerAnno.needHead()).flush();
            return;
        }
        if (o instanceof ExcelWriteWrapper) {
            ExcelWriteWrapper wrapper = (ExcelWriteWrapper) o;
            if (wrapper.getWriteListeners() != null) {
                writer.addListener(wrapper.getWriteListeners());
            }
            if (wrapper.getBigTitle() != null) {
                writer.writeTitle(wrapper.getBigTitle());
            }
            writer.write(wrapper.getData(), writerAnno.sheet(), writerAnno.needHead(), wrapper.getBoxValues())
                    .flush();
            return;
        }
        throw new ExcelResolverException("Method return value type must be ExcelWriteWrapper: " + methodParameter.getExecutable().getName());
    }
}
