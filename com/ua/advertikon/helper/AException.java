package com.ua.advertikon.helper;

public class AException extends Exception {
	public AException( String message ) {
		super( message );
	}

	public AException( String message, Exception e ) {
		super( message, e );
	}
}