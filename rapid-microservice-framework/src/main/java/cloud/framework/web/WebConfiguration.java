package cloud.framework.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Web配置类
 *
 * @author xmc
 */
@EnableWebMvc
@ConditionalOnProperty(
        name = {"spring.configurer.enabled"},
        havingValue = "true"
)
@Configuration
public class WebConfiguration implements WebMvcConfigurer, EnvironmentAware {

    /**
     * spring.configurer
     */
    private Map configurer = new HashMap();

    /**
     * 配置文件属性绑定器
     */
    private Binder binder;

    /**
     * 配置跨源请求
     */
    @Override
    public void addCorsMappings(CorsRegistry registry){
        if(this.configurer.get("crosmappings") != null){
            Map map = this.binder.bind("spring.configurer.crosmappings", Bindable.of(Map.class)).get();
            //设置允许跨域的路径，例如：/**
            registry.addMapping(map.get("mapping").toString())
                    //设置允许跨域请求的域名，例如：*
                    .allowedOrigins(map.get("origins").toString())
                    //设置允许的方法，例如：*
                    .allowedMethods(map.get("methods").toString())
                    .allowedHeaders(map.get("headers").toString())
                    //是否允许证书，例如：true
                    .allowCredentials(Boolean.parseBoolean(map.get("cookies").toString()));
        }
    }

    /**
     * 配置静态资源
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        if(this.configurer.get("resources") != null){
            List<Map> listMap = this.binder.bind("spring.configurer.resources", Bindable.listOf(Map.class)).get();
            for (Map map : listMap) {
                registry.addResourceHandler(map.get("handler").toString())
                        .addResourceLocations(map.get("location").toString());
            }
        }
    }

    /**
     * 配置视图解析器
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry){
        if(this.configurer.get("views") != null){
            List<Map> listMap = this.binder.bind("spring.configurer.views", Bindable.listOf(Map.class)).get();
            for (Map map : listMap) {
                if(map.get("viewname") == null){
                    registry.addViewController(map.get("path").toString());
                }else{
                    registry.addViewController(map.get("path").toString()).setViewName(map.get("viewname").toString());
                }
            }
        }
    }

    /**
     * 配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        if(this.configurer.get("interceptors") != null){
            List<String> list = this.binder.bind("spring.configurer.interceptors", Bindable.listOf(String.class)).get();
            for (String item : list) {
                Class classItem = null;
                try {
                    classItem = Class.forName(item);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if(classItem != null){
                    boolean isAbstract = Modifier.isAbstract(classItem.getModifiers());
                    if (!isAbstract && HandlerInterceptorAdapter.class.isAssignableFrom(classItem)) {
                        InterceptorPattens interceptorPattens = (InterceptorPattens)classItem.getAnnotation(InterceptorPattens.class);
                        if (interceptorPattens != null) {
                            HandlerInterceptorAdapter handlerInterceptor = null;
                            try {
                                handlerInterceptor = (HandlerInterceptorAdapter)classItem.newInstance();
                            } catch (IllegalAccessException | InstantiationException e) {
                                continue;
                            }
                            if (handlerInterceptor != null) {
                                InterceptorRegistration result = registry.addInterceptor(handlerInterceptor).addPathPatterns(interceptorPattens.value());
                                String[] excluePath = interceptorPattens.excludePath();
                                if (excluePath != null && excluePath.length != 0) {
                                    result.excludePathPatterns(excluePath);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 配置异步请求超时时间
     */
    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer){
        Object timeout = this.configurer.get("request-timeout");
        if(timeout != null){
            configurer.setDefaultTimeout(Long.parseLong(timeout.toString()));
            configurer.registerCallableInterceptors(new CallableProcessingInterceptor[]{this.timeoutInterceptor()});
        }
    }

    /**
     * 注册可调用处理超时拦截器
     */
    @Bean
    public TimeoutCallableProcessingInterceptor timeoutInterceptor() {
        return new TimeoutCallableProcessingInterceptor();
    }

    /**
     * 绑定配置文件属性
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.binder = Binder.get(environment);
        this.configurer = (Map)this.binder.bind("spring.configurer", Bindable.of(Map.class)).get();
    }

}