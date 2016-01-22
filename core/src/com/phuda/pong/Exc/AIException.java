package com.phuda.pong.Exc;

public class AIException extends Exception {
	
	String errorStr;
	
	public AIException(String errorDescription)
	{
		this.errorStr = errorDescription;
	}
	
	public String toString()
	{
		return errorStr;
	}
}
