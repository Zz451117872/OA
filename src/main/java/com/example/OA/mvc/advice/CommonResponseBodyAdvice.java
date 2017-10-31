package com.example.OA.mvc.advice;

import com.example.OA.mvc.common.ServerResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Type;

/**
 * Created by aa on 2017/10/31.
 */
@ControllerAdvice
public class CommonResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> aClass) {
        Type type = methodParameter.getGenericParameterType();
        boolean noAware = ServerResponse.class.equals(type) || String.class.equals(type);
        return !noAware;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        ServerResponse result = new ServerResponse();
        result.setStatus(true);
        result.setData(body);
        return result;
    }
}
