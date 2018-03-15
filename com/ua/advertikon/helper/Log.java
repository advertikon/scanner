package com.ua.advertikon.helper;

public class Log {
	static private String ColorRed    = "\u001b[0;91m";
	static private String ColorGreen  = "\u001b[0;92m";
	static private String ColorYellow = "\u001b[0;93m";
	static private String ColorWhite  = "\u001b[0;97m";
	static private String ColorBlue   = "\u001b[0;94m";
	static private String ColorEnd    = "\u001b[0m";

	static public void error( String message ) {
		System.err.println( ColorRed + message + ColorEnd );
	}

	static public void error( Exception message ) {
		System.err.println( ColorRed + message + ColorEnd );
	}

	static public void debug( String message ) {
		System.out.println( message );
	}

	static public void debug( int message ) {
		System.out.println( message );
	}

	static public void info( String message ) {
		System.err.print( message );
	}

	static public void exit( String message ) {
		System.err.println( ColorRed + message + ColorEnd );
		System.exit( 1 );
	}

	static public void exit( Exception message ) {
		System.err.println( ColorRed + message + ColorEnd );
		System.exit( 1 );
	}

	static public void dump( String[] d ) {
		for( int i = 0; i < d.length; i++ ) {
			System.out.println( d[ i ] );
		}
	}
}
