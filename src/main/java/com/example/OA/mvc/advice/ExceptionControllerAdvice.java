package com.example.OA.mvc.advice;

import com.example.OA.mvc.common.ServerResponse;
import com.example.OA.mvc.exception.AppException;
import com.example.OA.mvc.exception.AuthorizationException;
import com.example.OA.mvc.exception.Error;
import org.apache.shiro.authz.UnauthorizedException;
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

    Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(AppException.class)
    public ServerResponse appException(AppException ex) {
        return appExceptionToServerResponse(ex);
    }

    @ExceptionHandler(AuthorizationException.class)
    public ServerResponse authException(HttpServletResponse response,AuthorizationException ex) {
        response.setStatus(401);
        logger.error(ex.getMessage(), ex);
        return appExceptionToServerResponse(new AppException(Error.UN_AUTHENTICATION,"未认证"));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ServerResponse authz(HttpServletResponse response ,UnauthorizedException ex) {
        response.setStatus(401);
        logger.error(ex.getMessage(), ex);
        return appExceptionToServerResponse(new AppException(Error.UN_AUTHORIZATION,"未授权"));
    }

    @ExceptionHandler(Exception.class)
    public ServerResponse exception(Exception ex, HttpServletResponse response) {
        response.setStatus(500);
        logger.error(ex.getMessage(), ex);
        return appExceptionToServerResponse(new AppException(Error.UNKNOW_EXCEPTION));
    }

    private ServerResponse appExceptionToServerResponse(AppException ex) {
        ServerResponse serverResponse = ServerResponse.createByError();
        serverResponse.setCode(ex.getCode());
        serverResponse.setMsg(ex.getMessage());
        return serverResponse;
    }
}
