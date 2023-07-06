package cloud.framework.web;

import java.lang.annotation.*;

/**
 * 拦截器路径配置注解
 *
 * @author xmc
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface InterceptorPattens {

    /**
     * 拦截路径
     */
    String[] value() default {"/**"};

    /**
     * 放行路径
     */
    String[] excludePath() default {};

}