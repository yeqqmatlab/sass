package com.zsy.sass.common.exception;

/**
 * 调用外部HTTP接口异常
 *
 * @author Live.InPast
 * @date 2018/10/23
 */
public class ZSYApiException extends RuntimeException {

    /**
     * 构造函数
     * @param errMsg
     * @param cause
     */
    public ZSYApiException(String errMsg, Throwable cause) {
        super(errMsg,cause);
    }

    /**
     * 构造函数
     * @param errMsg
     */
    public ZSYApiException(String errMsg) {
        super(errMsg);
    }
}
