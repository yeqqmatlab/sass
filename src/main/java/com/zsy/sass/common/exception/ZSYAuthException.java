package com.zsy.sass.common.exception;

/**
 * 授权失败
 *
 * @author Live.InPast
 * @date 2018/10/23
 */
public class ZSYAuthException extends RuntimeException {

    /**
     * 构造函数
     */
    public ZSYAuthException() {

    }

    /**
     * 构造函数
     * @param errMsg
     */
    public ZSYAuthException(String errMsg) {
        super(errMsg);
    }

    /**
     * 构造函数
     * @param errMsg
     * @param cause
     */
    public ZSYAuthException(String errMsg, Throwable cause) {
        super(errMsg,cause);
    }

}
