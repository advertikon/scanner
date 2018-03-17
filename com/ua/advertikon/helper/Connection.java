package com.ua.advertikon.helper;

import java.net.*;
import java.io.*;

public class Connection {
	public URL url = null;
	protected String page = "";
	protected String method = "GET";
	protected String data = "";
	public HttpURLConnection connection = null;
	public BufferedReader reader = null;

	public Connection ( String page ) {
		this.page = page;
		init();
	}

	public Connection ( String page, String method, String data ) {
		this.page = page;
		this.method = method;
		this.data = data;
		init();
	}

	protected void init() {
		try {
			url = new URL( page );
			Log.debug( "Connection to " + page );

			connection = (HttpURLConnection)url.openConnection();
			HttpURLConnection.setFollowRedirects( true );
			connection.setRequestMethod( method );
			connection.setReadTimeout( 5000 );
			connection.addRequestProperty( "Accept-Language", "en-US,en;q=0.8" );
			connection.addRequestProperty( "User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36" /*"Mozilla"*/ );
			connection.addRequestProperty( "Referer", "google.com" );
			AUrl.setCookie( connection );

			if ( method.equals( "POST" ) ) {
				AUrl.setPost( connection, data );
			}

			int code = connection.getResponseCode();

			try {
				AUrl.saveCookie( connection );
				
			} catch ( AException e ) {
				Log.error( e );
			}

			if ( code >= 300 && code < 400 ) {
				String location = connection.getHeaderField( "Location" );

				if ( null == location ) {
					Log.error( "Redirect location is missing" );
					return;
				}

				Log.debug( "Redirected to " + location );

				page = location;
				init();
			}

			reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );

		} catch ( MalformedURLException e ) {
			Log.error( e );

		} catch ( FileNotFoundException e ) {
			Log.error( e );

		} catch ( IOException e ) {
			Log.error( e );
		}
	}

	public String readLine() {
		try {
			return reader.readLine();

		} catch ( IOException e ) {
			Log.error( e );
		}

		return null;
	}
}