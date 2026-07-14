package com.example.common;

public class R<T> {
    private int code;
    private String message;
    private T data;

    private R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功
    public static <T> R<T> success(T data) {
        return new R<>(200, "success", data);
    }

    // 失败
    public static <T> R<T> error(String message) {
        return new R<>(400, message, null);
    }

    // 服务器错误
    public static <T> R<T> error(int code, String message) {
        return new R<>(code, message, null);
    }

    // Getter
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}