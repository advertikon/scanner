package com.ua.advertikon.helper;

import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

public class AUrl {
	static final protected String COOKIE_PATH = "cookie/";

	static public String get( String page ) {
		String ret = "";
		URL url = null;
		HttpURLConnection connection = null;

		try {
			url = new URL( page );
			Log.debug( "URL: " + url );
			
		} catch ( MalformedURLException e ) {
			Log.error( e );
			return ret;
		}

		try {
			connection = (HttpURLConnection)url.openConnection();
			ret = read( connection );
				
		} catch ( IOException e ) {
			Log.error( e );
		}

		return ret;
	}

	static public String read( HttpURLConnection c ) throws IOException {
		StringBuffer out = new StringBuffer();
		String line = null;
		BufferedReader reader = new BufferedReader( new InputStreamReader( c.getInputStream() ) );

		Log.debug( "Response message: " + c.getResponseMessage() );

		while ( null != ( line = reader.readLine() ) ) {
			out.append( line + "\n" );
		}

		reader.close();
		return out.toString();
	}

	static public void dumpHeaders( URLConnection c ) {
		Map<String, List<String>> headers = c.getHeaderFields();
		String[] s = {};

		System.out.println( "----------- Response headers dump -------------" );

		for( Map.Entry<String, List<String>> entry: headers.entrySet() ) {
			System.out.println( entry.getKey() + ": " + implode( ", ", entry.getValue().toArray( s) ) );
		}

		System.out.println( "-------------------- End -----------------------" );
	}

	static public void dumpRequestHeaders( URLConnection c ) {
		Map<String, List<String>> headers = c.getRequestProperties();
		String[] s = {};

		System.out.println( "----------- Request headers dump -------------" );

		for( Map.Entry<String, List<String>> entry: headers.entrySet() ) {
			System.out.println( entry.getKey() + ": " + implode( ", ", entry.getValue().toArray( s) ) );
		}

		System.out.println( "-------------------- End -----------------------" );
	}

	static public String implode ( String delimiter, String[] parts ) {
		StringBuffer out = new StringBuffer( parts.length );

		for( int i = 0; i < parts.length; i++ ) {
			if ( i != parts.length - 1 ) {
				out.append( parts[ i ] + delimiter );

			} else {
				out.append( parts[ i ] );
			}
		}

		return out.toString();
	}

	public static List<HashMap<String, String>> getCookie( URLConnection c ) {
		Map<String, List<String>> headers = c.getHeaderFields();
		List<String> list = headers.get( "Set-Cookie" );

		List<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();

		for ( String item: list ) {
			HashMap<String, String> cookie = new HashMap<String, String>();
			String[] items = item.split( ";" );

			for( int i = 0; i < items.length; i++ ) {
				String[] data = items[ i ].split( "=" );

				if ( data.length > 1 ) {
					if ( i == 0 ) {
						cookie.put( "name", data[ 0 ].trim()  );
						cookie.put( "value", data[ 1 ].trim() );

					} else {
						if ( data[ 0 ].trim().equals( "expires" ) ) {
							data[ 1 ] = LocalDateTime.parse( data[ 1 ], DateTimeFormatter.ofPattern( "E',' d'-'LLL'-'y H':'m':'s 'GMT'" ) ).toString();
						}

						cookie.put( data[ 0 ].trim(), data[ 1 ] );
					}

				} else if ( data.length == 1 ) {
					cookie.put( data[ 0 ].trim(), "" );
				}

				cookie.put( "now", LocalDateTime.now().toString() );

				if ( null == cookie.get( "domain" ) ) {
					cookie.put( "domain", c.getURL().getHost() );
				}
			}

			ret.add( cookie );
		}

		return ret;
	}

	public static boolean setPost( URLConnection c, String data ) {
		try {
			c.setDoOutput( true );
			DataOutputStream wr = new DataOutputStream( c.getOutputStream() );
			wr.writeBytes( data );
			wr.flush();
			wr.close();
			
		} catch ( IOException e ) {
			Log.error( e );
			return false;
		}

		return true;
	}

	public static void saveCookie( URLConnection c ) throws AException {
		List<HashMap<String, String>> cookie = getCookie( c );
		boolean found = false;

		for( HashMap<String, String> item: cookieFromJar( c ) ) {
			found = false;

			for( HashMap<String, String> cookie_item: cookie ) {
				if ( cookie_item.get( "name" ).equals( item.get( "name" ) ) ) {
					found = true;
					break;
				}
			}

			if ( !found ) {
				cookie.add( item );
			}
		}

		if ( cookie.size() == 0 ) return;

		File path = new File( COOKIE_PATH );

		if ( !path.exists() ) {
			if( !path.mkdirs() ) {
				throw new AException( String.format( "Failed to create folder %s", path.getAbsolutePath() ) );
			}

		} else {
			if ( !path.isDirectory() ) {
				throw new AException( String.format( "%s is not a folder", path.getAbsolutePath() ) );
			}
		}

		Stash.serialize( cookie, COOKIE_PATH + c.getURL().getHost().replace( "/", "_" ) );
	}

	static public boolean setCookie( URLConnection c ) {
		String host = c.getURL().getHost();
		StringBuffer cookie_line = new StringBuffer();

		if ( null == host || host.equals( "" ) ) {
			return false;
		}

		int count = 0;

		for( HashMap<String, String> item: cookieFromJar( c ) ) {
			if ( 0 != cookie_line.length() ) {
				cookie_line.append( ";" );
			}

			cookie_line.append( item.get( "name" ) + "=" + item.get( "value" ) );
			count++;
		}

		c.setRequestProperty( "Cookie", cookie_line.toString() );

		Log.debug( String.format( "%d cookies have been set", count ) );

		return true;
		
	}

	static public List<HashMap<String, String>> cookieFromJar( URLConnection c ) {
		String host = c.getURL().getHost();
		List<HashMap<String, String>> defCookie = new ArrayList<HashMap<String, String>>();

		if ( null == host || host.equals( "" ) ) {
			return defCookie;
		}

		File file = new File( COOKIE_PATH + host.replace( "/", "_" ) );

		if ( file.exists() ) {
			Log.debug( String.format( "Cookie file exists for host %s", host ) );

			List<HashMap<String, String>> cookie = (List<HashMap<String, String>>)Stash.unserialize( file.getAbsolutePath() );

			if ( null == cookie ) {
				Log.error( "Failed to retrieve cookie for host " + host );

			} else {
				defCookie = cookie;
			}
		}

		return defCookie;
	}
}