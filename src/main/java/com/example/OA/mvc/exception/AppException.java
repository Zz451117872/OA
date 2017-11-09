package com.example.OA.mvc.exception;

public class AppException extends RuntimeException{

    private static final long serialVersionUID = 2404372373182554123L;
    private int code;
	private String msg;

	public AppException() {
	    this(Error.UNKNOW_EXCEPTION);
	}

	public AppException(Error error) {
		if(error != null)
		{
			this.code = error.getCode();
			this.msg = error.getMsg();
		}else {
			this.code = Error.UNKNOW_EXCEPTION.getCode();
			this.msg = Error.UNKNOW_EXCEPTION.getMsg();
		}
	}

	public AppException(Error error, String exMsg) {
		this.code = error.getCode();
		this.msg = exMsg;
	}

	@Override
	public String getMessage() {
		return msg;
	}

	public int getCode() {
		return code;
	}

}
