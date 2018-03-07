package com.ua.advertikon.helper;

public class Log {
	private String ColorRed    = "\u001b[0;91m";
	private String ColorGreen  = "\u001b[0;92m";
	private String ColorYellow = "\u001b[0;93m";
	private String ColorWhite  = "\u001b[0;97m";
	private String ColorBlue   = "\u001b[0;94m";
	private String ColorEnd    = "\u001b[0m";

	public void error( String message ) {
		System.err.println( ColorRed + message + ColorEnd );
	}

	public void error( Exception message ) {
		System.err.println( ColorRed + message + ColorEnd );
	}

	public void debug( String message ) {
		System.out.println( message );
	}

	public void info( String message ) {
		System.err.print( message );
	}

	public void exit( String message ) {
		System.err.println( ColorRed + message + ColorEnd );
		System.exit( 1 );
	}
}