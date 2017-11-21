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
 * 统一返回值处理
 */
@ControllerAdvice
public class CommonResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> aClass) {
        /*
        拦截被 ResponseBody 注释的方法。在结果输出到响应前做些处理
        方法的 返回值 类型 如果 是 定义的 ServerResponse ，则不拦截。
         */
        Type type = methodParameter.getMethod().getGenericReturnType();
        return !type.getTypeName().equals(ServerResponse.class.getTypeName());
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        /*
        拦截后，统一处理返回值为 ServerResponse 类型。
         */
        ServerResponse result = ServerResponse.createBySuccess(body);
        return result;
    }
}
