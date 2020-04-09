package cn.gjing.tools.excel.driven;

import cn.gjing.tools.excel.ExcelFactory;
import cn.gjing.tools.excel.exception.ExcelInitException;
import cn.gjing.tools.excel.read.resolver.ExcelReader;
import cn.gjing.tools.excel.write.resolver.ExcelWriter;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author Gjing
 **/
@Aspect
class ExcelDrivenHandler {
    @Resource
    private HttpServletResponse response;

    @Pointcut("@annotation(ExcelWrite)")
    private void excelWriteCut() {

    }

    @Pointcut("@annotation(ExcelRead)")
    public void excelReadCut() {

    }

    @AfterReturning(value = "excelWriteCut()", returning = "result")
    public void doWrite(JoinPoint joinPoint, Object result) {
        if (result instanceof WriteMetadata) {
            WriteMetadata writeMetadata = (WriteMetadata) result;
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            ExcelWrite excelWrite = method.getAnnotation(ExcelWrite.class);
            ExcelWriter writer = ExcelFactory.createWriter(excelWrite.value(), this.response, excelWrite.initDefaultStyle(), writeMetadata.getIgnores());
            if (excelWrite.multiHead()) {
                writer.enableMultiHead();
            }
            if (excelWrite.valid()) {
                writer.enableValid();
            }
            if (writeMetadata.getListeners() != null) {
                writer.addListener(writeMetadata.getListeners());
            }
            writer.write(writeMetadata.getData(), excelWrite.sheet(), excelWrite.needHead());
            throw new ExcelExport(writer);
        }
        throw new ExcelInitException("Method return value error, must be WriteData");
    }

    @SuppressWarnings("unchecked")
    @AfterReturning(value = "excelReadCut()", returning = "result")
    public void doRead(JoinPoint joinPoint, Object result) {
        if (result instanceof ReadMetadata) {
            ReadMetadata readMetadata = (ReadMetadata) result;
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            ExcelRead excelRead = method.getAnnotation(ExcelRead.class);
            ExcelReader<?> reader = ExcelFactory.createReader(readMetadata.getInputStream(), excelRead.value());
            if (readMetadata.getReadListeners() != null) {
                reader.addListener(readMetadata.getReadListeners());
                reader.collect(excelRead.collect());
            }
            if (readMetadata.getResultReadListener() != null) {
                reader.subscribe(readMetadata.getResultReadListener());
            }
        }
    }

}
