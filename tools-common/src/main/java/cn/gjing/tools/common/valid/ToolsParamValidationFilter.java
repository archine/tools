package cn.gjing.tools.common.valid;

import org.springframework.http.MediaType;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author Gjing
 **/
class ToolsParamValidationFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        if (MediaType.APPLICATION_JSON_VALUE.equals(servletRequest.getContentType())) {
            HttpServletRequest request = (HttpServletRequest) servletRequest;
            filterChain.doFilter(new ToolsParamValidationServletRequest(request), servletResponse);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }

    @Override
    public void destroy() {

    }
}
