package com.zsy.sass.common.exception;

/**
 * Token验证失败异常
 *
 * @author Live.InPast
 * @date 2018/10/23
 */
public class ZSYTokenValidException extends RuntimeException {

    /**
     * 构造函数
     */
    public ZSYTokenValidException() {

    }

    /**
     * 构造函数
     * @param errMsg
     */
    public ZSYTokenValidException(String errMsg) {
        super(errMsg);
    }

    /**
     * 构造函数
     * @param errMsg
     * @param cause
     */
    public ZSYTokenValidException(String errMsg, Throwable cause) {
        super(errMsg,cause);
    }

}
