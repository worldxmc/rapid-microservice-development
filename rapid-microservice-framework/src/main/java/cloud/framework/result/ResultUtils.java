package cloud.framework.result;

/**
 * 统一返回结果工具类
 * @author xmc
 */
public class ResultUtils {

	/**
	 * 执行成功，返回提示消息
	 */
	public static <T> Result<T> success(String msg){
		return new Result<T>().setCode(Result.SUCCESS).setMessage(msg);
	}
	
	/**
	 * 执行成功，返回提示消息和数据
	 */
	public static <T> Result<T> success(String msg, T t){
		return new Result<T>().setCode(Result.SUCCESS).setMessage(msg).setData(t);
	}
	
	/**
	 * 执行失败，返回提示消息
	 */
	public static <T> Result<T> fail(String msg){
		return new Result<T>().setCode(Result.FAIL).setMessage(msg);
	}

	/**
	 * 执行失败，返回错误码和提示消息
	 */
	public static <T> Result<T> fail(Integer code, String msg){
		return new Result<T>().setCode(code).setMessage(msg);
	}

}