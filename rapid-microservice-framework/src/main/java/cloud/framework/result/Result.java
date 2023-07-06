package cloud.framework.result;

import java.io.Serializable;

/**
 * 统一返回结果
 * @param <T> 结果集类型
 * @author xmc
 */
public class Result<T> implements Serializable {

    /**
     * 序列化ID
     */
    private static final long serialVersionUID = 1L;
    /**
     * 成功状态码
     */
    public static final Integer SUCCESS = 200;
    /**
     * 失败状态码
     */
    public static final Integer FAIL = 500;
    /**
     * 服务器内部错误状态码
     */
    public static final Integer SERVER_INTERNAL_ERROR = 700;

    /**
     * 状态码
     */
    private int code;
    /**
     * 消息内容
     */
    private String message;
    /**
     * 结果集
     */
    private T data;

    public Result() {
    }
    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }

    public T getData() {
        return this.data;
    }

    public Result<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "Result(code=" + this.getCode() + ", message=" + this.getMessage() + ", data=" + this.getData() + ")";
    }

}