package com.example.OA.mvc.advice;

import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.AuthorizationException;
import com.example.OA.mvc.exception.Error;
import com.example.OA.mvc.exception.ParamValidException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

/**
 * Created by aa on 2017/10/31.
 * 统一异常处理
 */
@ControllerAdvice
@ResponseBody
public class ExceptionControllerAdvice {

    Logger log = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(AppException.class)
    public ServerResponse appException(AppException ex) {
        return appExceptionToServerResponse(ex);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ServerResponse authException(HttpServletResponse response) {
        response.setStatus(401);
        return appExceptionToServerResponse(new AppException(Error.UN_AUTHORIZATION));
    }

    @ExceptionHandler(ParamValidException.class)
    public ServerResponse paramValidExceptionHandler(ParamValidException ex, HttpServletResponse response) {
        response.setStatus(400);
        ServerResponse result = appException(new AppException(Error.INVALID_PARAMS, ex.getMessage()));
        result.setData(ex.getFieldErrors());
        return result;
    }

    @ExceptionHandler(BindException.class)
    public ServerResponse bindExceptionHandler(BindException ex, HttpServletResponse response){
        return paramValidExceptionHandler(new ParamValidException(ex), response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ServerResponse constraintViolationExceptionHandler(ConstraintViolationException ex, HttpServletResponse response, HandlerMethod handlerMethod) {
        return paramValidExceptionHandler(new ParamValidException(ex, handlerMethod.getMethodParameters()), response);
    }

    @ExceptionHandler(Exception.class)
    public ServerResponse exception(Exception ex, HttpServletResponse response) {
        response.setStatus(500);
        log.error(ex.getMessage(), ex);
        return appExceptionToServerResponse(new AppException(Error.UNKNOW_EXCEPTION));
    }

    private ServerResponse appExceptionToServerResponse(AppException ex) {
        ServerResponse serverResponse = ServerResponse.createByError();
        serverResponse.setCode(ex.getCode());
        serverResponse.setMsg(ex.getMessage());
        return serverResponse;
    }
}
