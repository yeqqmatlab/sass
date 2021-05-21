package com.zsy.sass.common.exception;

/**
 * 禁止访问异常
 *
 * @author Live.InPast
 * @date 2018/11/30
 */
public class ZSYForbiddenException extends RuntimeException {

    /**
     * 构造函数
     * @param errMsg
     * @param cause
     */
    public ZSYForbiddenException(String errMsg, Throwable cause) {
        super(errMsg,cause);
    }

    /**
     * 构造函数
     * @param errMsg
     */
    public ZSYForbiddenException(String errMsg) {
        super(errMsg);
    }
}
