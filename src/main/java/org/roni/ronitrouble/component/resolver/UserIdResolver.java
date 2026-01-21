package org.roni.ronitrouble.component.resolver;

import org.jspecify.annotations.NonNull;
import org.roni.ronitrouble.annotation.UserId;
import org.roni.ronitrouble.exception.BusinessException;
import org.roni.ronitrouble.exception.exceptions.AuthError;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@Component
public class UserIdResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserId.class) && parameter.getParameterType().equals(Integer.class);
    }

    @Override
    public Integer resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        return Integer.parseInt(Objects.requireNonNull(webRequest.getUserPrincipal(), () -> {
            throw new BusinessException(AuthError.NO_AUTH);
        }).getName());
    }

}
