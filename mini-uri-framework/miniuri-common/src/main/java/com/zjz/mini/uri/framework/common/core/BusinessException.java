package com.zjz.mini.uri.framework.common.core;

import java.io.Serial;

/**
 * 业务异常
 * @author 19002
 */
public class BusinessException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 1L;
    private String code;
    private String msg;


    public BusinessException() {
    }

    public BusinessException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BusinessException(String code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
