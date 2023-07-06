package cloud.framework.exception;

import cloud.framework.result.Result;
import cloud.framework.result.ResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 统一异常处理器
 * @author xmc
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler({RestfulException.class})
    public Result exceptionHandler(RestfulException e) {
        log.error("发生业务异常！原因是：", e);
        return ResultUtils.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler({Exception.class})
    public Result exceptionHandler(Exception e) {
        log.error("发生未知异常！原因是：", e);
        return ResultUtils.fail(Result.SERVER_INTERNAL_ERROR, "服务器内部错误，请稍后再试！");
    }

}