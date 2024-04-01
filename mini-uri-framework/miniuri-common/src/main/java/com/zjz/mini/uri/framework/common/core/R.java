package com.zjz.mini.uri.framework.common.core;

import java.io.Serializable;
import java.util.Objects;

/**
 * 返回结果包装
 * @author 19002
 */
public class R<T> implements Serializable {

    private static final int SUCCESS_CODE = 200;
    private static final String SUCCESS_MSG = "success";
    private static final int FAIL_CODE = 500;
    private static final String FAIL_MSG = "fail";
    private int code;
    private String msg;
    private T data;


    /**
     * 泛型类中的静态方法和静态变量不可以使用泛型类所声明的泛型类型参数
     * 因为泛型类中的泛型参数的实例化是在定义对象的时候指定的，而静态变量和静态方法不需要使用对象来调用。
     * 对象都没有创建，如何确定这个泛型参数是何种类型，所以当然是错误的。
     */

    /**
     * 在泛型方法中使用的T是自己在方法中定义的 T，而不是泛型类中的T。
     * @return
     * @param <T>
     */
    public static <T> R<T> ok() {
        return rest(null, SUCCESS_CODE, SUCCESS_MSG);
    }

    public static <T> R<T> ok(T data) {
        return rest(data, SUCCESS_CODE, SUCCESS_MSG);
    }
    public static <T> R<T> ok(T data, String msg) {
        return rest(data, SUCCESS_CODE, msg);
    }

    public static <T> R<T> ok(T data, int code, String msg) {
        return rest(data, code, msg);
    }

    public static <T> R<T> fail() {
        return rest(null, FAIL_CODE, FAIL_MSG);
    }

    public static <T> R<T> fail(int code, String msg) {
        return rest(null, code, msg);
    }

    public static <T> R<T> fail(T data, int code, String msg) {
        return rest(data, code, msg);
    }

    private static <T> R<T> rest(T data, int code, String msg) {
        R<T> r = new R<>();
        r.setData(data);
        r.setCode(code);
        r.setMsg(msg);
        return r;
    }



    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        R<?> r = (R<?>) o;
        return code == r.code && Objects.equals(msg, r.msg) && Objects.equals(data, r.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, msg, data);
    }

    @Override
    public String toString() {
        return "R{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
