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
	final private int READ_TIME_OUT = 10000;
	final private int CONNECT_TIME_OUT = 5000;


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
			connection.setUseCaches( false );
			connection.setReadTimeout( READ_TIME_OUT );
			connection.setConnectTimeout( CONNECT_TIME_OUT );
			connection.setInstanceFollowRedirects( true );
			connection.setRequestMethod( method );
			connection.addRequestProperty( "Accept-Language", "en-US,en;q=0.8" );
			connection.addRequestProperty( "User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36" );
			connection.addRequestProperty( "Referrer", "google.com" );
			AUrl.setCookie( connection );

			if ( method.equals( "POST" ) ) {
				AUrl.setPost( connection, data );
			}

			// AUrl.dumpHeaders( connection );

			int code = connection.getResponseCode();
			Log.debug( String.format( "Response: [%d] %s", code, connection.getResponseMessage() ) );

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
				connection.disconnect();
				init();
			}

			if ( code == 200 ) {
				reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
			}

		} catch ( MalformedURLException e ) {
			Log.error( e );

		} catch ( FileNotFoundException e ) {
			Log.error( e );

		} catch ( IOException e ) {
			Log.error( e );
		}
	}

	public boolean canRead() {
		boolean ret = false;

		try {
			ret = null != reader && reader.ready();

		} catch ( IOException e ) {
			Log.error( e ); 
		}

		return ret;
	}

	public String readLine() {
		try {
			return reader.readLine();

		} catch ( IOException e ) {
			Log.error( e );
		}

		return null;
	}

	public String readAll() {
		StringBuffer ret = new StringBuffer();
		int c = -1;

			try {
				if ( null != reader && reader.ready() ) {
					while ( -1 != ( c = reader.read() ) ) {
						ret.append( (char)c );
					}
				}

				if ( null != reader ) {
					reader.close();
				}

			} catch ( IOException e ) {
				Log.error( e );
			}

		return ret.toString();
	}

	public void open() {
		try {
			connection.connect();

		} catch ( IOException e ) {
			Log.error( e );
		}
	}

	public void disconnect() {
		connection.disconnect();
	}

	static public void socket() {
		try ( Socket socket = new Socket( "oc.ua", 80) ) {
			// InetAddress addr = InetAddress.getByName("oc.ua");
			
			boolean autoflush = true;
			PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);
			BufferedReader in = new BufferedReader(

			new InputStreamReader(socket.getInputStream()));
			// send an HTTP request to the web server
			out.print("GET /index.php?route=extension/module/adk_mail/log/ HTTP/1.1\r\n");
			out.print("Host: oc.ua\r\n");
			out.print("Connection: Close\r\n");
			out.print( "\r\n" );

			// read the response
			boolean loop = true;
			StringBuilder sb = new StringBuilder(8096);
			while (loop) {
				if (in.ready()) {
					int i = 0;
					while (i != -1) {
						i = in.read();
						sb.append((char) i);
					}
					loop = false;
				}
			}
			System.out.println(sb.toString());
			
		} catch ( Exception e ) {
			Log.error( e );
		}
	}
}