package cloud.framework.exception;

import cloud.framework.result.Result;

/**
 * 自定义业务异常类
 * @author xmc
 */
public class RestfulException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private Integer code;

    private String message;

    public RestfulException(String message) {
        this.code = Result.FAIL;
        this.message = message;
    }

    public RestfulException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RestfulException{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

}