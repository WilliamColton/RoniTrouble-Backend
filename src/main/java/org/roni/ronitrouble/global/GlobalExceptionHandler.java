package org.roni.ronitrouble.global;


import org.roni.ronitrouble.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice("org.roni.ronitrouble")
public class GlobalExceptionHandler {

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<GlobalResp<Object>> exceptionHandle(BusinessException businessException) {
        if (businessException.getCode() == 401) {
            return new ResponseEntity<>(GlobalResp.error(businessException.getCode(), businessException.getMessage()), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(GlobalResp.error(businessException.getCode(), businessException.getMessage()), HttpStatus.BAD_REQUEST);
    }

}
