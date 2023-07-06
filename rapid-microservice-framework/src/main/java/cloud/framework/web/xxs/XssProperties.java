package cloud.framework.web.xxs;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 防xss攻击属性类
 *
 * @author xmc
 */
@ConfigurationProperties("spring.xss")
public class XssProperties {

    /**
     * 是否开启功能
     */
    private String enabled;

    /**
     * 排除内容
     */
    private String excludes;

    /**
     * url匹配规则
     */
    private String urlPatterns;

    /**
     * 响应头
     */
    private String contentType;

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public String getExcludes() {
        return excludes;
    }

    public void setExcludes(String excludes) {
        this.excludes = excludes;
    }

    public String getUrlPatterns() {
        return urlPatterns;
    }

    public void setUrlPatterns(String urlPatterns) {
        this.urlPatterns = urlPatterns;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        return "XssProperties{" +
                "enabled='" + enabled + '\'' +
                ", excludes='" + excludes + '\'' +
                ", urlPatterns='" + urlPatterns + '\'' +
                ", contentType='" + contentType + '\'' +
                '}';
    }

}