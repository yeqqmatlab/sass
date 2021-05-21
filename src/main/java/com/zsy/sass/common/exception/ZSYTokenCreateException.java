package com.zsy.sass.common.exception;

/**
 * Token授权失败异常
 *
 * @author Live.InPast
 * @date 2018/11/16
 */
public class ZSYTokenCreateException extends RuntimeException {

    /**
     * 构造函数
     */
    public ZSYTokenCreateException() {

    }

    /**
     * 构造函数
     * @param errMsg
     */
    public ZSYTokenCreateException(String errMsg) {
        super(errMsg);
    }

    /**
     * 构造函数
     * @param errMsg
     * @param cause
     */
    public ZSYTokenCreateException(String errMsg, Throwable cause) {
        super(errMsg,cause);
    }

}
