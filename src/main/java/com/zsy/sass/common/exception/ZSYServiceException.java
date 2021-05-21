package com.zsy.sass.common.exception;

/**
 * 业务异常
 *
 * @author Live.InPast
 * @date 2018/10/23
 */
public class ZSYServiceException extends RuntimeException {


    /**
     * 构造函数
     * @param errMsg
     * @param cause
     */
    public ZSYServiceException(String errMsg, Throwable cause) {
        super(errMsg,cause);
    }

    /**
     * 构造函数
     * @param errMsg
     */
    public ZSYServiceException(String errMsg) {
        super(errMsg);
    }

}
