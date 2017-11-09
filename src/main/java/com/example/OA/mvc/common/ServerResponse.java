package com.example.OA.mvc.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;


@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable{

    private boolean status;
    private int code;
    private String msg;
    private T data;

    public ServerResponse(){}

    private ServerResponse(boolean status)
    {
        this.status = status;
    }

    private ServerResponse(boolean status,T data)
    {
        this.status = status;
        this.data = data;
    }
    private ServerResponse(boolean status,String msg,T data)
    {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
    private ServerResponse(boolean status,String msg)
    {
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(boolean status,int code,String msg)
    {
        this.status = status;
        this.code = code;
        this.msg = msg;
    }
  //  @JsonIgnore
 //   public boolean isSuccess()
  //  {
  //      return this.status == true;
  //  }
    public boolean getStatus()
    {
        return status;
    }

    public String getMsg()
    {
        return msg;
    }
    public T getData()
    {
        return data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setData(T data) {
        this.data = data;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public  static  <T> ServerResponse<T> createBySuccess()
    {
        return new ServerResponse<T>(true);
    }
    public  static  <T> ServerResponse<T> createBySuccessMessage(String msg)
    {
        return new ServerResponse<T>(true,msg);
    }
    public  static  <T> ServerResponse<T> createBySuccess(T data)
    {
        return new ServerResponse<T>(true,data);
    }
    public  static  <T> ServerResponse<T> createBySuccess(String msg,T data)
    {
        return new ServerResponse<T>(true,msg,data);
    }
    public static <T> ServerResponse<T> createByError()
    {
        return new ServerResponse<T>(false);
    }
    public static <T> ServerResponse<T> createByErrorMessage(String msg)
    {
        return new ServerResponse<T>(false,msg);
    }
    public static <T> ServerResponse<T> createByErrorCodeMessage(int code,String msg)
    {
        return new ServerResponse<T>(false,code,msg);
    }

}
