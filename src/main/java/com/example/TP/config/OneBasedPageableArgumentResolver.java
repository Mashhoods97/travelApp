package com.example.TP.config;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class OneBasedPageableArgumentResolver extends PageableHandlerMethodArgumentResolver {

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter,
                                    ModelAndViewContainer mavContainer,
                                    NativeWebRequest webRequest,
                                    WebDataBinderFactory binderFactory) {
        Pageable pageable = (Pageable) super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);

        // Convert 1-based to 0-based
        if (pageable.getPageNumber() > 0) {
            return PageRequest.of(
                    pageable.getPageNumber() - 1,
                    pageable.getPageSize(),
                    pageable.getSort()
            );
        }
        return pageable;
    }
}
