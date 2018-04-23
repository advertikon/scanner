package ua.com.advertikon.helper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
	private int redirect;
	
	public static int REDIRECT_NONE = 0b00000000;
	public static int REDIRECT_SSL  = 0b00000001;
	public static int REDIRECT_ALL  = 0b01111111;

	public Connection ( String page ) {
		this.reader = null;
		this.connection = null;
		this.url = null;
		this.page = page;
		redirect = REDIRECT_SSL;
		init();
	}

	public Connection ( String page, String method, String data ) {
		this.reader = null;
		this.connection = null;
		this.url = null;
		this.page = page;
		this.method = method;
		this.data = data;
		redirect = REDIRECT_SSL;
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

			 AUrl.dumpHeaders( connection );

			int code = getConnection().getResponseCode();
			Log.debug(String.format("Response: [%d] %s", code, getConnection().getResponseMessage() ) );

			try {
				AUrl.saveCookie(getConnection());
				
			} catch ( AException e ) {
				Log.error( e );
			}

			if ( code >= 300 && code < 400 ) {
				if ( redirect == REDIRECT_NONE ) {
					throw new IOException( "Redirect detected. Configuration does't support redirects" );
				}

				String location = getConnection().getHeaderField( "Location" );

				if ( null == location ) {
					throw new IOException( "Redirect location is missing" );
				}
				
				Log.debug( "Redirect to: " + location );
				
				if ( redirect != REDIRECT_ALL ) {
					URL oldUrl = new URL( page );
					URL newUrl = new URL( location );
					
					Log.debug(  "old URL" );
					Log.debug( AUrl.dumpURL( oldUrl ) );
					Log.debug( "new URl" );
					Log.debug( AUrl.dumpURL( newUrl ) );
					
					if ( redirect == REDIRECT_SSL ) {
						if ( !oldUrl.getProtocol().equals( "http" ) || !newUrl.getProtocol().equals( "https" ) ) {
							throw new IOException( "Rederect detected. Configuration allows only redirect to SSL page (protocol failure)" );
						}
						
						if ( !oldUrl.getHost().equals( newUrl.getHost() ) || !oldUrl.getFile().equals( newUrl.getFile() ) ) {
							throw new IOException( "Rederect detected. Configuration allows only redirect to SSL page" );
						}
					}
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