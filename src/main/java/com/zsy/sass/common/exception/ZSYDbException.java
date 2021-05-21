package com.zsy.sass.common.exception;

/**
 * 数据库操作异常
 *
 * @author Live.InPast
 * @date 20118/10/23
 */
public class ZSYDbException extends RuntimeException {

    /**
     * 构造函数
     * @param errMsg
     * @param cause
     */
    public ZSYDbException(String errMsg, Throwable cause) {
        super(errMsg,cause);
    }

    /**
     * 构造函数
     * @param errMsg
     */
    public ZSYDbException(String errMsg) {
        super(errMsg);
    }
}
