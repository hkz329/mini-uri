package com.zjz.mini.uri.run.domain.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 通用API响应类
 *
 * @author hkz329
 */
@Data
@Accessors(chain = true)
public class ApiResponse<T> {
    
    private Integer code;
    private String msg;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>()
                .setCode(200)
                .setMsg("操作成功")
                .setData(data);
    }

    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<T>()
                .setCode(200)
                .setMsg(msg)
                .setData(data);
    }

    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<T>()
                .setCode(400)
                .setMsg(msg)
                .setData(null);
    }

    public static <T> ApiResponse<T> error(Integer code, String msg) {
        return new ApiResponse<T>()
                .setCode(code)
                .setMsg(msg)
                .setData(null);
    }

    public static <T> ApiResponse<T> unauthorized(String msg) {
        return new ApiResponse<T>()
                .setCode(401)
                .setMsg(msg)
                .setData(null);
    }
}