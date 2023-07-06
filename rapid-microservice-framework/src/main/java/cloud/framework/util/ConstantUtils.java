package cloud.framework.util;

/**
 * 常量工具类
 *
 * @author xmc
 */
public class ConstantUtils {
	
	/**
	 * ticket
	 */
	public static final String TICKET = "token";
	/**
	 * userSession
	 */
	public static final String USER_SESSION = "userSession";
	/**
	 * options
	 */
	public static final String OPTIONS = "OPTIONS";
	/**
	 * 请求类型
	 */
	public static final String GET = "GET";
	public static final String POST = "POST";

	/**
	 * 操作系统枚举
	 */
	public enum OSType {
		MAC,
		WINDOWS,
		LINUX;
	}

	/**
	 * 会话信息存储位置
	 */
	public enum SessionStorage {
		/**
		 * session
		 */
		session,
		/**
		 * redis
		 */
		redis;
	}

}