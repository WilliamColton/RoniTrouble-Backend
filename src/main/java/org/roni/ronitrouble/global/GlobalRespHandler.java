package org.roni.ronitrouble.global;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalRespHandler implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(@NotNull MethodParameter returnType, @NotNull Class converterType) {
        if (returnType.getMethod() == null) {
            return true;
        }
        System.out.println("Advice sees returnType.getParameterType() = " + returnType.getParameterType());
        System.out.println("Method raw return type                = " + returnType.getMethod().getReturnType());
        System.out.println("Method generic return type            = " + returnType.getMethod().getGenericReturnType());
        System.out.println("Selected converterType                = " + converterType);
        Class<?> type = returnType.getMethod().getReturnType();
        if (SseEmitter.class.isAssignableFrom(type)) {
            return false;
        } else if (ResponseEntity.class.isAssignableFrom(type)) {
            return false;
        } else if (Flux.class.isAssignableFrom(type)) {
            return false;
        } else return !GlobalResp.class.isAssignableFrom(type);
    }

    @Override
    public Object beforeBodyWrite(Object body, @NotNull MethodParameter returnType, @NotNull MediaType selectedContentType, @NotNull Class selectedConverterType, @NotNull ServerHttpRequest request, @NotNull ServerHttpResponse response) {
        if (returnType.getParameterType() == String.class) {
            // 因为 String 不会使用 json 消息转换器
            try {
                return objectMapper.writeValueAsString(GlobalResp.success(body));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return GlobalResp.success(body);
        }
    }

}