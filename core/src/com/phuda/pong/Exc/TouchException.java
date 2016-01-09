package com.phuda.pong.Exc;

public class TouchException extends Exception {
	
	String errorStr;
	
	public TouchException(String errorDescription)
	{
		this.errorStr = errorDescription;
	}
	
	public String toString()
	{
		return errorStr;
	}
}
