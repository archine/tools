package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.ExcelFactory;
import cn.gjing.tools.excel.exception.ExcelResolverException;
import cn.gjing.tools.excel.write.BigTitle;
import cn.gjing.tools.excel.write.resolver.ExcelBindWriter;
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
        if (writerAnno == null) {
            return;
        }
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);
        modelAndViewContainer.setRequestHandled(true);
        if (o instanceof ExcelWriteWrapper) {
            ExcelWriteWrapper wrapper = (ExcelWriteWrapper) o;
            ExcelBindWriter writer = ExcelFactory.createWriter(wrapper.getFileName() == null ? writerAnno.value() : wrapper.getFileName(), writerAnno.mapping(), response,
                    writerAnno.initDefaultStyle(), wrapper.getIgnores() == null ? writerAnno.ignores() : wrapper.getIgnores())
                    .valid(writerAnno.needValid())
                    .bind(wrapper.getUnqKey())
                    .multiHead(writerAnno.multiHead())
                    .addListener(wrapper.getWriteListeners());
            if (wrapper.getDataList().isEmpty()) {
                writer.write(null).flush();
                return;
            }
            boolean needHead = writerAnno.needHead();
            for (Object e : wrapper.getDataList()) {
                if (e == null) {
                    writer.write(null, writerAnno.sheet(), needHead, wrapper.getBoxValues());
                }
                if (e instanceof BigTitle) {
                    writer.writeTitle((BigTitle) e);
                    continue;
                }
                if (e instanceof List) {
                    writer.write((List<?>) e, writerAnno.sheet(), needHead, wrapper.getBoxValues());
                    needHead = false;
                    continue;
                }
                throw new ExcelResolverException("Invalid wrapper data type, currently supported BigTitle and List");
            }
            writer.flush();
            return;
        }
        if (o == null) {
            ExcelFactory.createWriter(writerAnno.value(), writerAnno.mapping(), response, writerAnno.initDefaultStyle(), writerAnno.ignores())
                    .valid(writerAnno.needValid())
                    .bind(writerAnno.bind())
                    .multiHead(writerAnno.multiHead())
                    .write(null, writerAnno.sheet(), writerAnno.needHead())
                    .flush();
            return;
        }
        if (o instanceof Collection) {
            ExcelFactory.createWriter(writerAnno.value(), writerAnno.mapping(), response, writerAnno.initDefaultStyle(), writerAnno.ignores())
                    .valid(writerAnno.needValid())
                    .bind(writerAnno.bind())
                    .multiHead(writerAnno.multiHead())
                    .write((List<?>) o, writerAnno.sheet(), writerAnno.needHead())
                    .flush();
            return;
        }
        if (o instanceof BigTitle) {
            ExcelFactory.createWriter(writerAnno.value(), writerAnno.mapping(), response, writerAnno.initDefaultStyle(), writerAnno.ignores())
                    .valid(writerAnno.needValid())
                    .bind(writerAnno.bind())
                    .multiHead(writerAnno.multiHead())
                    .writeTitle((BigTitle) o)
                    .write(null, writerAnno.sheet(), writerAnno.needHead())
                    .flush();
            return;
        }
        throw new ExcelResolverException("Method return value type is not supported, currently supported BigTitle and Collection and ExcelWriteWrapper: " + methodParameter.getExecutable().getName());
    }
}
