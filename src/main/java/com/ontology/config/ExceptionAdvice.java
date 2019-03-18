package com.ontology.config;

import com.ontology.exception.OntIdException;
import com.ontology.model.Result;
import com.ontology.utils.Constant;
import com.ontology.utils.ErrorInfo;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * 异常控制处理器
 */
@RestControllerAdvice
public class ExceptionAdvice {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

//    /**
//     * 捕捉UnauthorizedException自定义异常
//     */
//    @ResponseStatus(HttpStatus.UNAUTHORIZED)
//    @ExceptionHandler(OntIdException.class)
//    public Result handle401(OntIdException e, HttpServletRequest httpServletRequest) {
////        String action, long error, String desc, Object result, String version
//        if("zh".equals(httpServletRequest.getHeader(Constant.HTTPHEADER_LANGUAGE))) {
//            return new Result(e.getAction(),e.getErrCode(),e.getErrDesCN(), "");
//        }else {
//            return new Result(e.getAction(),e.getErrCode(),e.getErrDesEN(), "");
//        }
//    }

    /**
     * 捕捉校验异常(BindException)
     *
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public Result validException(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, Object> result = this.getValidError(fieldErrors);
        return new Result(HttpStatus.BAD_REQUEST.getReasonPhrase(), ErrorInfo.PARAM_ERROR.code(), result.get("errorMsg").toString(), "");
    }

    /**
     * 捕捉校验异常(MethodArgumentNotValidException)
     *
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result validException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        Map<String, Object> result = this.getValidError(fieldErrors);
        return new Result(HttpStatus.BAD_REQUEST.getReasonPhrase(), ErrorInfo.PARAM_ERROR.code(), result.get("errorMsg").toString(), "");
    }

    /**
     * 捕捉自定义业务类异常
     * 需要区分中英文
     *
     * @return
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OntIdException.class)
    public Result handle(OntIdException e, HttpServletRequest httpServletRequest) {
        if ("zh".equals(httpServletRequest.getHeader(Constant.HTTPHEADER_LANGUAGE))) {
            return new Result(e.getAction(), e.getErrCode(), e.getErrDesCN(), "");
        } else {
            return new Result(e.getAction(), e.getErrCode(), e.getErrDesEN(), "");
        }

    }

    /**
     * 捕捉404异常
     *
     * @return
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result handle(NoHandlerFoundException e) {
        return new Result(HttpStatus.NOT_FOUND.getReasonPhrase(), ErrorInfo.PARAM_ERROR.code(), e.getMessage(), null);
    }

    /**
     * 捕捉其他所有异常
     *
     * @param request
     * @param ex
     * @return
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Result globalException(HttpServletRequest request, Throwable ex) {
        logger.error("error...", ex);
        return new Result(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), ErrorInfo.PARAM_ERROR.code(), ex.getMessage(), null);
    }

    /**
     * 获取状态码
     *
     * @param request
     * @return
     */
    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }

    /**
     * 获取校验错误信息
     *
     * @param fieldErrors
     * @return
     */
    private Map<String, Object> getValidError(List<FieldError> fieldErrors) {
        Map<String, Object> result = new HashMap<String, Object>(16);
        List<String> errorList = new ArrayList<String>();
        StringBuffer errorMsg = new StringBuffer("校验异常(ValidException):");
        for (FieldError error : fieldErrors) {
            errorList.add(error.getField() + "-" + error.getDefaultMessage());
            errorMsg.append(error.getField() + "-" + error.getDefaultMessage() + ".");
        }
        result.put("errorList", errorList);
        result.put("errorMsg", errorMsg);
        return result;
    }
}
