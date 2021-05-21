package com.zsy.sass.common.exception;

/**
 * 服务异常
 *
 * @author Live.InPast
 * @date 2018/10/23
 */
public class ZSYServerException extends RuntimeException {

    /**
     * 构造函数
     * @param errMsg
     * @param cause
     */
    public ZSYServerException(String errMsg, Throwable cause) {
        super(errMsg,cause);
    }

    /**
     * 构造函数
     * @param errMsg
     */
    public ZSYServerException(String errMsg) {
        super(errMsg);
    }
}
