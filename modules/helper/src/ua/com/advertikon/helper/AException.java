package ua.com.advertikon.helper;

public class AException extends Exception {

	private static final long serialVersionUID = 1L;
	public AException( String message ) {
		super( message );
	}

	public AException( String message, Exception e ) {
		super( message, e );
	}
}