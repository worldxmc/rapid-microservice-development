package cloud.framework.web.xxs;

import cloud.framework.exception.RestfulException;
import cloud.framework.result.Result;
import cloud.framework.util.ConstantUtils;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 防xss攻击过滤器
 *
 * @author xmc
 */
public class XssFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(XssFilter.class);

    private List<String> excludes = Lists.newArrayList();

    private List<String> contentTypes = Lists.newArrayList();

    public boolean enabled = false;

    public XssFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
        String tmpEnabled = filterConfig.getInitParameter("enabled");
        String tmpExcludes = filterConfig.getInitParameter("excludes");
        String tmpContentType = filterConfig.getInitParameter("contentType");
        this.excludes = Splitter.on(",").omitEmptyStrings().splitToList(tmpExcludes);
        this.enabled = Boolean.valueOf(tmpEnabled);
        this.contentTypes = Splitter.on(",").omitEmptyStrings().splitToList(tmpContentType);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String param = "";
        HttpServletRequest req = (HttpServletRequest)request;
        String contentType = req.getContentType();
        String path = req.getRequestURI().substring(req.getContextPath().length()).replaceAll("[/]+$", "");
        if (this.excludes.stream().anyMatch((exclude) -> Pattern.matches(exclude, path))) {
            chain.doFilter(request, response);
        } else if (this.contentTypes.stream().anyMatch((type) -> contentType.contains(type))) {
            chain.doFilter(request, response);
        } else {
            XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper(req);
            if (ConstantUtils.POST.equalsIgnoreCase(req.getMethod())) {
                param = this.getBodyString(xssRequest.getReader());
                if (StringUtils.isNotBlank(param) && xssRequest.checkXSSAndSql(param)) {
                    log.error(param);
                    throw new RestfulException(Result.FAIL, "XSS or SQL injection attack");
                }
            }
            if (xssRequest.checkParameter()) {
                log.error(param);
                throw new RestfulException(Result.FAIL, "XSS or SQL injection attack");
            }
            chain.doFilter(xssRequest, response);
        }
    }

    private String getBodyString(BufferedReader br) {
        String str = "";
        try {
            String inputLine;
            while((inputLine = br.readLine()) != null) {
                str = str + inputLine;
            }
            br.close();
        } catch (IOException ioException) {
            System.out.println("IOException: " + ioException);
        }
        return str;
    }

    @Override
    public void destroy() {
    }

}