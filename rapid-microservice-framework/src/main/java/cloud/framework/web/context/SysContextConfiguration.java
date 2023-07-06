package cloud.framework.web.context;

import cloud.framework.util.ConstantUtils;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

/**
 * 系统上下文配置类
 *
 * @author xmc
 */
@Configuration
@ConditionalOnProperty(
        name = {"spring.context.type"}
)
@EnableConfigurationProperties({SysContextProperties.class})
public class SysContextConfiguration {

    @Autowired
    private SysContextProperties sysContextProperties;

    @Bean
    public SysContext getSysContext() {
        String type = this.sysContextProperties.getType();
        Assert.notNull(type, "{spring.context.type} can not be empty");
        Assert.state(EnumUtils.isValidEnum(ConstantUtils.SessionStorage.class, type), "{spring.context.type} is incorrect");
        SysContext sysContext = new SysContext(
                this.sysContextProperties.getType(),
                this.sysContextProperties.getSid(),
                this.sysContextProperties.getUid(),
                this.sysContextProperties.getTicket());
        return sysContext;
    }

}