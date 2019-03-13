package com.zh.netty.model;

public enum ResultCode {
	/* 成功状态码 */
    SUCCESS(1, "成功"),
    SUCCESS_INIE(2,"初始化成功"),
    TYPE_PONE(0,"pong"),
    TYPE_SUCCESS_JOIN(51, "进入场景成功"), 
    TYPE_SUCCESS_LEAVE(52, "离开场景成功"), 
    TYPE_ERROR_UNKNOW(-3000, "未知错误"),
    TYPE_ERROR_PARAMS(-3001, "参数错误"),
    TYPE_ERROR_UNAUTHORIZED(-3002, "身份认证失败"),
    TYPE_ERROR_FORBIDDEN(-3003, "没有权限"),
    TYPE_ERROR_TIMEOUT(-3004, "链接超时"),
    SCENE_DIG_HLC(301, "首页"),

    TYPE_ERROR_INVALID_IDENTITY(-3004, "用户状态无效（被冻结）");
    
    private Integer code;

    private String msg;
    
    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public Integer code() {
        return this.code;
    }

    public String message() {
        return this.msg;
    }
    
    public void setMessage(String msg){
    	this.msg = msg;
    }
    
    public static String getMessage(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.msg;
            }
        }
        return name;
    }

    public static Integer getCode(String name) {
        for (ResultCode item : ResultCode.values()) {
            if (item.name().equals(name)) {
                return item.code;
            }
        }
        return null;
    }
    

}
