package cloud.framework.web.context;

import cloud.framework.cache.RedisUtils;
import cloud.framework.util.ConstantUtils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统上下文
 *
 * @author xmc
 */
public class SysContext {

    private static final String USER_ID = "userId";
    private static final String USER_NAME = "userName";
    private static final String REAL_NAME = "realName";
    private static final String ORG_NAME = "orgName";
    private static final String ROLE_IDS = "roleIds";
    private static final String PRIVILEGE_TREE = "privilegeTree";

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

    /**
     * 票据持有人
     */
    private String holder;

    @Autowired(required = false)
    private RedisUtils redisUtils;

    public SysContext(String type, String sid, String uid, String ticket) {
        this.type = type;
        this.sid = sid;
        this.uid = uid;
        this.ticket = ticket;
    }

    /**
     * 获取当前用户userId
     */
    public String getCurrentUserId() {
        return getUserSession(USER_ID);
    }

    /**
     * 获取当前用户userName
     */
    public String getCurrentUserName() {
        return getUserSession(USER_NAME);
    }

    /**
     * 获取当前用户realName
     */
    public String getCurrentRealName() {
        return getUserSession(REAL_NAME);
    }

    /**
     * 获取当前用户orgName
     */
    public String getCurrentOrgName() {
        return getUserSession(ORG_NAME);
    }

    /**
     * 获取当前用户会话信息
     */
    private String getUserSession(String key) {
        this.setHolderByTicket();
        String sessionKey = String.format("%s:%s", this.sid, this.holder);
        Object sessionValue = null;
        ConstantUtils.SessionStorage item = EnumUtils.getEnum(ConstantUtils.SessionStorage.class, this.type);
        switch (item){
            case session:
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                Object attribute = request.getSession().getAttribute(sessionKey);
                if(attribute != null){
                    JSONObject jsonObject = JSONObject.parseObject(attribute.toString());
                    sessionValue = jsonObject.get(key);
                }
                break;
            case redis:
                sessionValue = this.redisUtils.hashGet(sessionKey).get(key);
                break;
            default:
                break;
        }
        return sessionValue != null ? sessionValue.toString() : "";
    }

    /**
     * 设置票据持有人
     */
    private void setHolderByTicket() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String holder = request.getHeader(this.ticket);
        Assert.notNull(holder, "当前请求中未能获取到票据信息");
        this.setHolder(holder);
    }

    /**
     * 获取当前用户roleIds
     */
    public Object getCurrentUserRoleIds() {
        return getUserInfo(ROLE_IDS);
    }

    /**
     * 获取当前用户privilegeTree
     */
    public Object getCurrentUserPrivilegeTree() {
        return getUserInfo(PRIVILEGE_TREE);
    }

    /**
     * 获取当前用户用户信息
     */
    private Object getUserInfo(String key) {
        String userId = getUserSession(USER_ID);
        String userInfoKey = String.format("%s:%s", this.uid, userId);
        Object userInfoValue = null;
        ConstantUtils.SessionStorage item = EnumUtils.getEnum(ConstantUtils.SessionStorage.class, this.type);
        switch (item){
            case session:
                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                Object attribute = request.getSession().getAttribute(userInfoKey);
                if(attribute != null){
                    JSONObject jsonObject = JSONObject.parseObject(attribute.toString());
                    userInfoValue = jsonObject.get(key);
                }
                break;
            case redis:
                userInfoValue = this.redisUtils.hashGet(userInfoKey).get(key);
                break;
            default:
                break;
        }
        return userInfoValue;
    }

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

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

}