package org.roni.ronitrouble.global;


import lombok.extern.slf4j.Slf4j;
import org.roni.ronitrouble.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice("org.roni.ronitrouble")
public class GlobalExceptionHandler {

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<GlobalResp<Object>> exceptionHandle(BusinessException businessException) {
        if (businessException.getCode() == 401) {
            log.info("账号未登录！");
            return new ResponseEntity<>(GlobalResp.error(businessException.getCode(), businessException.getMessage()), HttpStatus.UNAUTHORIZED);
        }
        log.info(businessException.getMessage());
        return new ResponseEntity<>(GlobalResp.error(businessException.getCode(), businessException.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
