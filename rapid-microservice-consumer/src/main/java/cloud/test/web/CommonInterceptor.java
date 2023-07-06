package cloud.test.web;

import cloud.framework.cache.RedisUtils;
import cloud.framework.util.ConstantUtils;
import cloud.framework.web.InterceptorPattens;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 自定义公共拦截器
 *
 * @author xmc
 */
@InterceptorPattens(excludePath = {
        "/", "/index.html", "/pages/**", "/js/**", "/dist/**", "/index/*"
})
public class CommonInterceptor extends HandlerInterceptorAdapter {

    /**
     * session过期时间（分钟）
     */
    private static final long USER_SESSION_EXPIRE = 30L;
    /**
     * 拦截请求进行前置处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 设置支持跨域
        setResponseHeader(request, response);
        // 过滤掉前置请求
        if (!ConstantUtils.OPTIONS.equals(request.getMethod())) {
            String token = request.getHeader(ConstantUtils.TICKET);
            if(StringUtils.isBlank(token)){
                try {
                    response.sendError(401);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
            BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
            RedisUtils redisUtils = factory.getBean(RedisUtils.class);
            Map<Object, Object> sessionMap = redisUtils.hashGet(ConstantUtils.USER_SESSION + token);
            if (sessionMap == null || sessionMap.isEmpty()) {
                try {
                    response.sendError(401);
                }catch (Exception e){
                    e.printStackTrace();
                }
                return false;
            }
            // 刷新session过期时间
            redisUtils.expire(ConstantUtils.USER_SESSION + token, USER_SESSION_EXPIRE * 60);
        }
        return true;
    }

    /**
     * 设置请求头支持跨域
     */
    private void setResponseHeader(HttpServletRequest request, HttpServletResponse response) {
        if (request.getHeader(HttpHeaders.ORIGIN) != null) {
            // 需要注意addHeader与setHeader的区别
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("origin"));
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, HEAD");
            response.setHeader("Access-Control-Allow-Headers", "Content-Type");
            response.setHeader("Access-Control-Max-Age", "3600");
            // 允许前台传递自定义请求头content-type,token
            response.setHeader("Access-Control-Allow-Headers", "content-type,token");
        }
    }

}