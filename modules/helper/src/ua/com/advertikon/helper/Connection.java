package ua.com.advertikon.helper;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Connection {
	private URL url;
	private String page   = "";
	private String method = "GET";
	private String data   = "";
	private HttpURLConnection connection;
	private BufferedReader reader;
	final private int READ_TIME_OUT  = 10000;
	final private int CONNECT_TIME_OUT = 5000;
	private static Logger logger = Logger.getLogger( Connection.class.getName() );

	public Connection ( String page ) {
		this.reader = null;
		this.connection = null;
		this.url = null;
		this.page = page;
		init();
	}

	public Connection ( String page, String method, String data ) {
		this.reader = null;
		this.connection = null;
		this.url = null;
		this.page = page;
		this.method = method;
		this.data = data;
		init();
	}

	protected void init() {

		try {
			setUrl(new URL( page ));
			Log.debug( "Connection to " + page );

			setConnection((HttpURLConnection)getUrl().openConnection());
			getConnection().setUseCaches( false );
			getConnection().setReadTimeout( READ_TIME_OUT );
			getConnection().setConnectTimeout( CONNECT_TIME_OUT );
			getConnection().setInstanceFollowRedirects( true );
			getConnection().setRequestMethod( method );
			getConnection().addRequestProperty( "Accept-Language", "en-US,en;q=0.8" );
			getConnection().addRequestProperty( "User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36" );
			getConnection().addRequestProperty( "Referrer", "google.com" );
			AUrl.setCookie(getConnection());

			if ( method.equals( "POST" ) ) {
				AUrl.setPost(getConnection(), data );
			}

			// AUrl.dumpHeaders( connection );

			int code = getConnection().getResponseCode();
			Log.debug(String.format("Response: [%d] %s", code, getConnection().getResponseMessage() ) );

			try {
				AUrl.saveCookie(getConnection());
				
			} catch ( AException e ) {
				Log.error( e );
			}

			if ( code >= 300 && code < 400 ) {
				String location = getConnection().getHeaderField( "Location" );

				if ( null == location ) {
					Log.error( "Redirect location is missing" );
					return;
				}

				Log.debug( "Redirected to " + location );

				page = location;
				getConnection().disconnect();
				init();
			}

			if ( code == 200 ) {
				setReader(new BufferedReader( new InputStreamReader( getConnection().getInputStream() ) ));
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
			ret = null != getReader() && getReader().ready();

		} catch ( IOException e ) {
			Log.error( e ); 
		}

		return ret;
	}

	public String readLine() {
		try {
			return getReader().readLine();

		} catch ( IOException e ) {
			Log.error( e );
		}

		return null;
	}

	public String readAll() {
		StringBuilder ret = new StringBuilder();
		int c = -1;

			try {
				if ( null != getReader() && getReader().ready() ) {
					while ( -1 != ( c = getReader().read() ) ) {
						ret.append( (char)c );
					}
				}

				if ( null != getReader() ) {
					getReader().close();
				}

			} catch ( IOException e ) {
				Log.error( e );
			}

		return ret.toString();
	}

	public void open() {
		try {
			getConnection().connect();

		} catch ( IOException e ) {
			Log.error( e );
		}
	}

	public void disconnect() {
		getConnection().disconnect();
	}

	/**
	 * @return the url
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * @return the connection
	 */
	public HttpURLConnection getConnection() {
		return connection;
	}

	/**
	 * @param connection the connection to set
	 */
	public void setConnection(HttpURLConnection connection) {
		this.connection = connection;
	}

	/**
	 * @return the reader
	 */
	public BufferedReader getReader() {
		return reader;
	}

	/**
	 * @param reader the reader to set
	 */
	public void setReader(BufferedReader reader) {
		this.reader = reader;
	}
}