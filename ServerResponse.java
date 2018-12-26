package com.imooc.demo.demo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

//保证序列化的时候，如果为null，key也会消失
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerResponse<T> {
    private int status;
    private String msg;
    private T data;

    public ServerResponse() {
    }

    public ServerResponse(int status){
        this.status = status;
    }
    public ServerResponse(int status, String msg){
        this.status = status;
        this.msg = msg;
    }
    public ServerResponse(int status, T data){
        this.status = status;
        this.data = data;
    }
    public ServerResponse(int status, String msg, T data){
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    //使之不在json序列化结果当中
    @JsonIgnore
    public boolean isSuccess(){
        return this.status == ResultCode.SUCCESS;
    }
    public int getStatus(){
        return status;
    }
    public T getData(){
        return data;
    }
    public String getMsg(){
        return msg;
    }

    public static <T> ServerResponse<T> createBySuccess(){
        return new ServerResponse<T>(ResultCode.SUCCESS);
    }
    public static <T> ServerResponse<T> createBySuccessMassage(String msg){
        return new ServerResponse<T>(ResultCode.SUCCESS, msg);
    }
    public static <T> ServerResponse<T> createBySuccess(T data){
        return new ServerResponse<T>(ResultCode.SUCCESS, data);
    }
    public static <T> ServerResponse<T> createBySuccess(String msg, T data){
        return new ServerResponse<T>(ResultCode.SUCCESS, msg, data);
    }
    public static <T> ServerResponse<T> createByError(){
        return new ServerResponse<T>(ResultCode.FAIL);
    }
    public static <T> ServerResponse<T> createByError(String msg){
        return new ServerResponse<T>(ResultCode.FAIL, msg);
    }
    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorcode, String msg){
        return new ServerResponse<T>(errorcode, msg);
    }
}
