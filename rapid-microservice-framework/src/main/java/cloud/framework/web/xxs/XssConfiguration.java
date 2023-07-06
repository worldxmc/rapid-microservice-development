package cloud.framework.web.xxs;

import cloud.framework.cache.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.Arrays;
import java.util.HashMap;

/**
 * 防xss攻击配置类
 *
 * @author xmc
 */
@Configuration
@ConditionalOnProperty(
        name = {"spring.xss.enabled"},
        havingValue = "true"
)
@EnableConfigurationProperties({XssProperties.class})
public class XssConfiguration extends CachingConfigurerSupport {

    @Autowired
    private XssProperties xssProperties;

    public XssConfiguration() {
    }

    /**
     * 注册防xss攻击过滤器
     */
    @Bean
    public RedisUtils xssFilterRegistration() {
        FilterRegistrationBean<Filter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD);
        registrationBean.setFilter(new XssFilter());
        registrationBean.setName("xssFilter");
        registrationBean.setUrlPatterns(Arrays.asList(this.xssProperties.getUrlPatterns()));
        registrationBean.setOrder(Integer.MAX_VALUE);
        HashMap<String, String> initParameters = new HashMap<>();
        initParameters.put("excludes", this.xssProperties.getExcludes());
        initParameters.put("enabled", this.xssProperties.getEnabled());
        initParameters.put("contentType", this.xssProperties.getContentType());
        registrationBean.setInitParameters(initParameters);
        return new RedisUtils();
    }

}