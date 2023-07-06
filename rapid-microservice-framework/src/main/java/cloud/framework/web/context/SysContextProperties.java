package cloud.framework.web.context;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 系统上下文属性类
 *
 * @author xmc
 */
@ConfigurationProperties("spring.context")
public class SysContextProperties {

    /**
     * 会话信息存储位置名称
     */
    private String type;

    /**
     * 会话信息前缀
     */
    private String sid;

    /**
     * 用户信息前缀
     */
    private String uid;

    /**
     * 票据名称
     */
    private String ticket;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

}