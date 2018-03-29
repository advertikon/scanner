package ua.com.advertikon.helper;

import java.time.*;
import java.time.format.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log extends Logger {
	private static final String COLOR_RED    = "\u001b[0;91m";
	private static final String COLOR_GREEN  = "\u001b[0;92m";
	private static final String COLOR_YELLOW = "\u001b[0;93m";
	private static final String COLOR_WHITE  = "\u001b[0;97m";
	private static final String COLOR_BLUE   = "\u001b[0;94m";
	private static final String COLOR_END   = "\u001b[0m";
	
	final static public int LEVEL_DEBUG = 0;
	final static public int LEVEL_INFO = 10;
	final static public int LEVEL_ERROR = 20;
	
	private static Level logLevel = Level.WARNING;
	
	public Log( String name, String resourceBundleName ) {
		super( name, resourceBundleName );
	}
	
	public Log( String name ) {
		super( name, null );
	}

	static public void error( String message ) {
////		if ( logLevel > LEVEL_ERROR ) {
////			return;
////		}
		System.err.println( COLOR_RED + message + COLOR_END );
	}

	static public void error( Exception message ) {
////		if ( logLevel > LEVEL_ERROR ) {
////			return;
////		}
//
		System.err.println( COLOR_RED + message + COLOR_END );
		message.getCause().printStackTrace();
	}

	static public void debug( String message ) {
//		if ( logLevel > LEVEL_DEBUG ) {
//			return;
//		}

		System.out.println( message );
	}

	static public void debug( int message ) {
//		if ( logLevel > LEVEL_DEBUG ) {
//			return;
//		}

		System.out.println( message );
	}

//	static public void info( String message ) {
////		if ( logLevel > LEVEL_INFO ) {
////			return;
////		}
//
//		System.err.print( message );
//	}

	static public void exit( String message ) {
//		if ( logLevel > LEVEL_ERROR ) {
//			return;
//		}

		System.err.println( COLOR_RED + message + COLOR_END );
		System.exit( 1 );
	}

	static public void exit( Exception message ) {
//		if ( logLevel > LEVEL_ERROR ) {
//			return;
//		}
		System.err.println( COLOR_RED + message + COLOR_END );
		System.exit( 1 );
	}

	static public void dump( String[] d ) {
		for ( String d1 : d ) {
			System.out.println( d1 );
		}
	}
	
	static public void dump( byte[] d ) {
		System.out.println( "Size: " + d.length );

		for ( byte d1 : d ) {
			System.out.print( String.format( "%s ", Integer.toBinaryString( d1 ) ) );
		}
		
		System.out.println( "" );
	}
	
	static public void dumpAsBinary( byte[] d ) {
		System.out.println( "Size: " + d.length );

		for ( byte d1 : d ) {
			System.out.print( String.format( "%s ", Integer.toBinaryString( d1 ) ) );
		}
		
		System.out.println( "" );
	}
	
	static public void dumpAsChar( byte[] d ) {
		System.out.println( "Size: " + d.length );

		for ( byte d1 : d ) {
			System.out.print( String.format( "%c ", d1 & 0xff ) );
		}
		
		System.out.println( "" );
	}

	static public void testDateFormat( String format ) {
		System.out.println( LocalDateTime.now().format( DateTimeFormatter.ofPattern( format ) ) );
	}
}
