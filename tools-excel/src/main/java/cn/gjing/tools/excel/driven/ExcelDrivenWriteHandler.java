package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.ExcelFactory;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.write.BigTitle;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;

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
        assert writerAnno != null;
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        modelAndViewContainer.setRequestHandled(true);
        if (o instanceof ExcelWriteWrapper) {
            ExcelWriteWrapper wrapper = (ExcelWriteWrapper) o;
            ExcelFactory.createWriter(writerAnno.value(), writerAnno.mapping(), response, writerAnno.initDefaultStyle(), wrapper.getIgnores() == null ? writerAnno.ignores() : wrapper.getIgnores())
                    .valid(writerAnno.needValid())
                    .multiHead(writerAnno.multiHead())
                    .writeTitle(wrapper.getBigTitle())
                    .addListener(wrapper.getWriteListeners())
                    .write(wrapper.getData(), writerAnno.sheet(), writerAnno.needHead(), wrapper.getBoxValues())
                    .flush();
            return;
        }
        if (o == null) {
            ExcelFactory.createWriter(writerAnno.value(), writerAnno.mapping(), response, writerAnno.initDefaultStyle(), writerAnno.ignores())
                    .valid(writerAnno.needValid())
                    .multiHead(writerAnno.multiHead())
                    .write(null, writerAnno.sheet(), writerAnno.needHead())
                    .flush();
            return;
        }
        if (o instanceof Collection) {
            ExcelFactory.createWriter(writerAnno.value(), writerAnno.mapping(), response, writerAnno.initDefaultStyle(), writerAnno.ignores())
                    .valid(writerAnno.needValid())
                    .multiHead(writerAnno.multiHead())
                    .write((List<?>) o, writerAnno.sheet(), writerAnno.needHead())
                    .flush();
            return;
        }
        if (o instanceof BigTitle) {
            ExcelFactory.createWriter(writerAnno.value(), writerAnno.mapping(), response, writerAnno.initDefaultStyle(), writerAnno.ignores())
                    .valid(writerAnno.needValid())
                    .multiHead(writerAnno.multiHead())
                    .writeTitle((BigTitle) o)
                    .write(null, writerAnno.sheet(), writerAnno.needHead())
                    .flush();
            return;
        }
        throw new ExcelResolverException("Method return value type must be ExcelWriteWrapper: " + methodParameter.getExecutable().getName());
    }
}
