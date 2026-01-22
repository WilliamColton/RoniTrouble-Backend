package org.roni.ronitrouble.exception.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.roni.ronitrouble.exception.ErrorInfo;

@Getter
@RequiredArgsConstructor
public enum DataError implements ErrorInfo {

    LOGICAL_ERROR(999, "逻辑错误！"),
    THE_DATA_DOES_NOT_EXIST_ERROR(9999, "数据不存在！"),
    THE_DATA_ALREADY_EXISTS_ERROR(9998,"数据已存在！");

    private final int code;
    private final String message;

}