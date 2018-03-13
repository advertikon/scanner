package com.ua.advertikon.helper;

import java.net.*;
import java.io.*;

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
}