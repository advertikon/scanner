package com.ua.advertikon.helper;

import java.net.*;
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

public class AUrl {

	public String get( String page ) {
		StringBuffer out = new StringBuffer();
		String ret = "";
		URL url = null;
		HttpURLConnection connection = null;
		BufferedReader reader = null;
		String line = null;

		try {
			url = new URL( page );
			Log.debug( "URL: " + url );
			
		} catch ( MalformedURLException e ) {
			Log.error( e );

			return ret;
		}

		try {
			connection = (HttpURLConnection)url.openConnection();
			reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );

			Log.debug( "Response message: " + connection.getResponseMessage() );

			while ( null != ( line = reader.readLine() ) ) {
				out.append( line );
			}

			reader.close();
			ret = out.toString();
				
		} catch ( IOException e ) {
			Log.error( e );
		}

		return ret;
	}

	public void dumpHeaders( URLConnection c ) {
		Map<String, List<String>> headers = c.getHeaderFields();
		String[] s = {};

		for( Map.Entry<String, List<String>> entry: headers.entrySet() ) {
			System.out.println( entry.getKey() + ": " + implode( ", ", entry.getValue().toArray( s) ) );
		}
	}

	public String implode ( String delimiter, String[] parts ) {
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

	public List<HashMap<String, String>> getCookie( URLConnection c ) {
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
			}

			ret.add( cookie );
		}

		return ret;
	}
}