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
	private static final Logger L = Logger.getLogger( Connection.class.getName() );

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

	static public String socket( URL url, int port ) {
		L.log( Level.INFO, () -> String.format( "Connection to %s on port %d", url.getHost(), port ) );

		try ( Socket socket = new Socket( "oc.ua", 80 ) ) {
			L.log( Level.INFO, () -> {
				try {
					return String.format(
						"Socket is opened:%n" +
						"Local address: %s%n" +
						"Local port: %d%n" +
						"Local Socket address: %s%n" +
						"SO_KEEPALIVE: %s%n" +
						"SO_OOBINLINE: %s%n" +
						"SO_REUSABLEADDRESS: %s%n" +
						"SO_LINGER: %s%n" +
						"SO_TCONODELAY: %s%n" +
						"SO_TIMEOUT: %d%n" +
						"Recieve buffer: %s%n" +
						"Send buffer: %s%n",
						socket.getLocalAddress(),
						socket.getLocalPort(),
						socket.getLocalSocketAddress(),
						socket.getKeepAlive(),
						socket.getOOBInline(),
						socket.getReuseAddress(),
						socket.getLinger(),
						socket.getTcpNoDelay(),
						socket.getSoTimeout(),
						socket.getReceiveBufferSize(),
						socket.getSendBufferSize()
					);

				} catch ( SocketException e ) {
					return e.getMessage();
				}
			} );
			
			boolean autoflush = true;
			PrintWriter out = new PrintWriter(socket.getOutputStream(), autoflush);
			BufferedReader in = new BufferedReader(

			new InputStreamReader(socket.getInputStream()));
			// send an HTTP request to the web server
			out.print("GET /" + url.getPath() + "?" + url.getQuery() + " HTTP/1.1\r\n");
			out.print("Host: " + url.getHost() + "\r\n");
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
			
			return sb.toString();
			
		} catch ( IOException e ) {
			L.log( Level.SEVERE, null, e );
		}
		
		return "";
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