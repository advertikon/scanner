/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import static ua.com.advertikon.helper.AUrl.COOKIE_PATH;

/**
 *
 * @author max
 */
public class ASocket implements AutoCloseable {
	private static final Logger logger = Logger.getLogger( ASocket.class.getName() );
	private URL url;
	private final ArrayList<HashMap<String, String>> headers = new ArrayList<>();
	private final StringBuilder body = new StringBuilder();
	private final Pattern responcePattern = Pattern.compile( "http/\\S+\\s+(\\d+)\\s+(.+)$", Pattern.CASE_INSENSITIVE );
	private int responceCode = 0;
	private String responceMessage = "";
	private Socket socket;
	private boolean isSSL = false;
	final private ArrayList<HashMap<String, String>> cookie = new ArrayList<>();
	final private String COOKIE_PATH = "cookie/";
	
	protected String protocol = "http";
	protected String host = "";
	protected int port = 80;
	protected String path = "";
	protected String query = "";
	
	public ASocket() {

	}
	
	public ASocket( String site ) throws IOException {
		setUrl( site );
	}
	
	public void url( String site ) throws MalformedURLException {
		setUrl( site );
	}
	
	private void setUrl( String site ) throws MalformedURLException {
		int index = 0, position = 0;
		position = site.indexOf( "//", index );
		String rest = "";
		
		if ( position >= 0 ) {
			protocol = site.substring( index, position -1  );
			index = position + 2;
		}
		
		if ( site.charAt( index ) != '/' ) {
			position = site.indexOf( "/", index );
			
			if ( position > 0 ) {
				host = site.substring( index, position );
				index = position + 1;

			} else {
				position = site.length();
				host = site.substring( index, position );
				index = position;
			}
		}
		
		rest = site.substring( index );

		System.out.println( "protocol: " + protocol );
		System.out.println( "host: " + host );
		System.out.println( "rest: " + rest );
		
		System.out.println( getProtocol() + "://" + getHost() + "/" + rest );
		url = new URL( getProtocol() + "://" + getHost() + "/" + rest );
		protocol = url.getProtocol();
		host = url.getHost();
		port = url.getPort();
		path = url.getPath();
		query = url.getQuery();
		
		logger.log(  Level.FINE, this::dumpURL );
	}
	
	public void run() throws IOException, AException {
		if ( getHost().isEmpty() ) {
			throw new AException( "Host name is empty" );
		}

		getSocket();

		logger.log( Level.INFO, () -> String.format( "Connecting to %s on port %d", getHost(), getPort() ) );
		
		socket.connect( new InetSocketAddress( getHost(), getPort() ) );
		socket.setSoTimeout( 1000 );
		logger.log(  Level.FINEST, this::dumpSocketSettings );
		
		if ( isSSL ) {
			handshake(( SSLSocket ) socket);
		}

		PrintWriter out = new PrintWriter( socket.getOutputStream() );
		out.print( socketHeader() );
		out.flush();
		
		if ( isSSL ) {
			if ( out.checkError() ) {
				logger.warning( "SSLSocketClient:  java.io.PrintWriter error" );
			}
		}
		
		try ( BufferedReader in = new BufferedReader( new InputStreamReader( socket.getInputStream() ) ) ) {
			boolean isHeader = true;
			String line;

			try {
				while( null != ( line = in.readLine() ) ) {
					if ( isHeader ) {
						isHeader = readHeaderLine( line );

					} else {
						body.append( line ).append( "\n" );
					}
				}
				
			} catch ( SocketTimeoutException e ) {
				
			}
		
			out.close(); // TODO: may stay open when exception throwed - socket now should be closed
			logger.fine( this::dumpHeaders );
			cookieToStorage();

			if ( getResponceCode() >= 300 && getResponceCode() < 400 ) {
				String location = getHeader( "Location" );

				if ( location.isEmpty() ) {
					throw new AException( "Location header is empty" );
				}

				url = new URL( location );
				run();
			}
		}
	}
	
	protected void handshake( SSLSocket socket ) throws IOException {
		socket.startHandshake();
	} 
	
	public String socketHeader() {
		String out = "GET " + getFile() + " HTTP/1.1\r\n" +
					"Host: " + getHost() + "\r\n" +
//					"Connection: keep-alive\r\n" +
					"Connection: close\r\n" +
					"Accept-Language: en-US,en;q=0.5\r\n" +
					"Accept: text/html,application/xhtml+xm…plication/xml;q=0.9,*/*;q=0.8\r\n" +
					setCookieToHeader() +
					"User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36\r\n" +
//					"User-Agent: Mozilla/5.0 (X11; Ubuntu; Linu…) Gecko/20100101 Firefox/59.0\r\n" +
//					"User-Agent: FooAgent\r\n" +
//					"Accept-Encoding: gzip, deflate\r\n" +
					"Upgrade-Insecure-Requests: 1\r\n" +
					"\r\n";
		
		logger.log(  Level.FINE, () -> "Socket header OUT: \n" + out );
		
		return out;
	}
	
	protected boolean readHeaderLine( String line ) {
		boolean isHeader = true;

		if ( line.isEmpty() ) {
			isHeader = false;

		} else {
			HashMap<String, String> headerItem = new HashMap<>();
			int index;

			if ( ( index = line.indexOf( ":" ) ) > 0 ) {
				String name = line.substring( 0, index ).trim();
				String value = line.substring( index + 1 ).trim();

				headerItem.put( "name", name );
				headerItem.put( "value", value );

				if ( name.compareToIgnoreCase( "Set-Cookie" ) == 0 ) {
					addCookie( value );
				}

			} else {
				Matcher matcher = responcePattern.matcher( line );

				if ( matcher.find() ) {
					setResponceCode( matcher.group( 1 ) );
					setResponceMessage( matcher.group( 2 ) );
				}

				headerItem.put( "name", "" );
				headerItem.put( "value", line );
			}

			headers.add(  headerItem );
		}
		
		return isHeader;
	}
	
	protected String dumpSocketSettings() {
		String dump = "";

		try {
			dump = String.format(
				"Socket information:%n" +
				"Is connected: %s%n" +
				"Is bound: %s%n" +
				"Local Socket address: %s%n" +
				"SO_KEEPALIVE: %s%n" +
				"SO_REUSABLEADDRESS: %s%n" +
				"SO_LINGER: %s%n" +
				"SO_TCONODELAY: %s%n" +
				"SO_TIMEOUT: %d%n" +
				"Recieve buffer: %s%n" +
				"Send buffer: %s%n",
				socket.isConnected(),
				socket.isBound(),
				socket.getLocalSocketAddress(),
				socket.getKeepAlive(),
				socket.getReuseAddress(),
				socket.getSoLinger(),
				socket.getTcpNoDelay(),
				socket.getSoTimeout(),
				socket.getReceiveBufferSize(),
				socket.getSendBufferSize()
			);

		} catch ( SocketException e ) {
			logger.log(  Level.SEVERE, null, e );
		}
		
		return dump;
	}
	
	protected String dumpHeaders() {
		return "Headers Dump:\n" + headers.stream().map( i -> ( i.get( "name" ).isEmpty() ? "" : i.get(  "name" ) + ": " ) + i.get( "value" ) ).collect( Collectors.joining( "\n" ) );
	}

	/**
	 * @return the responceCode
	 */
	public int getResponceCode() {
		return responceCode;
	}

	/**
	 * @param responceCode the responceCode to set
	 */
	public void setResponceCode( String responceCode ) {
		this.responceCode = Integer.parseInt( responceCode );
	}

	/**
	 * @return the responceMessage
	 */
	public String getResponceMessage() {
		return responceMessage;
	}

	/**
	 * @param responceMessage the responceMessage to set
	 */
	public void setResponceMessage( String responceMessage ) {
		this.responceMessage = responceMessage;
	}
	
	public String getHeader( String name ) {
		return headers.stream().filter( ( i ) -> i.get( "name" ).equals( name ) ).map( i -> i.get( "value" ) ).findFirst().orElse( "" );
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port > 0 ? port : ( protocol.equals( "http" ) ? 80 : 443 );
	}
	
	public String getPath() {
		return !path.isEmpty() ? path : "/";
	}
	
	public String getQuery() {
		return null != query && !query.isEmpty() ? query : "";
	}
	
	public String getFile() {
		return url != null && !url.getFile().isEmpty() ? url.getFile() : getPath() + ( !getQuery().isEmpty() ? "?" + getQuery() : "" );
	}
	
	protected void getSocket() throws IOException {
		Socket ret;
		close();
		
		switch ( getPort() ) {
			case 443:
				SSLSocketFactory f = ( SSLSocketFactory ) SSLSocketFactory.getDefault();
				socket = f.createSocket();
			break;
			default:
				socket = new Socket();
			break;
		}
	}
	
	public String getBody() {
		return body.toString();
	}
	
	@Override
	public void close() {
		if ( null != socket ) {
			try {
				socket.close();
				socket = null;

			} catch ( IOException ex ) {
				Logger.getLogger( ASocket.class.getName() ).log( Level.SEVERE, null, ex );
			}
		}
	}
	
	/**
	 * Adds cookie record from header to inner in-memory storage
	 * @param line Header cookie string
	 */
	protected void addCookie( String line ) {
		String[] parts = line.split( ";" );
		
		if ( !parts[ 0 ].isEmpty() ) {
			String[] subparts = parts[ 0 ].split( "=" );
			
			if ( subparts.length > 1 ) {
				HashMap<String, String> c = new HashMap<>( 2 );
				c.put( "name", subparts[ 0 ] );
				c.put( "value", subparts[ 1 ] );
				_cookieAdd( c );
			}
		}
	}
	
	/**
	 * Adds cookie record to inner in-memory storage
	 * @param c Cookie
	 */
	public void addCookie( HashMap<String, String> c ) {
		_cookieAdd( c );
	}
	
	/**
	 * Retrieves cookie from storage if any
	 */
	protected void cookieFromStorage() {
		File file = new File( COOKIE_PATH + getHost().replace( "/", "_" ) );

		if ( file.exists() ) {
			logger.log( Level.FINE, () -> String.format( "Cookie file exists for host %s", getHost() ) );

			ArrayList<HashMap<String, String>> jarCookies = (ArrayList<HashMap<String, String>>)Stash.unserialize( file.getAbsolutePath() );
			
			if ( null == jarCookies ) {
				logger.log( Level.WARNING, "Failed to retrieve cookie for host {0}", getHost() );

			} else {
				jarCookies.forEach( c -> {
					String name = c.get( "name" );
					boolean found = false;
					
					for( int i = 0, l = cookie.size(); i < l; i++ ) {
						if ( cookie.get( i ).getOrDefault( "name", "" ).equals( name ) ) {
							found = true;
							break;
						}
					}
					
					if ( !found ) {
						cookie.add( c );
					}
				} );
			}
		}
	}
	
	/**
	 * Returns cookies for specific host
	 * @return 
	 */
	public ArrayList<HashMap<String, String>> getCookies() {
		File file = new File( COOKIE_PATH + getHost().replace( "/", "_" ) );

		if ( file.exists() ) {
			logger.log( Level.FINE, () -> String.format( "Cookie file exists for host %s", getHost() ) );

			@SuppressWarnings( "unchecked" )
			ArrayList<HashMap<String, String>> jarCookies = (ArrayList<HashMap<String, String>>)Stash.unserialize( file.getAbsolutePath() );
			
			if ( null == jarCookies ) {
				logger.log( Level.WARNING, "Failed to retrieve cookie for host {0}", getHost() );

			} else {
				return jarCookies;
			}
		}
		
		return new ArrayList<>();
	}
	
	/**
	 * Puts cookies to storage
	 */
	public void cookieToStorage() {
		cookieFromStorage();

		if ( cookie.size() > 0 ) {
			try {
				Files.createDirectories( Paths.get(  COOKIE_PATH ) );

			} catch ( IOException ex ) {
				Logger.getLogger( ASocket.class.getName() ).log( Level.SEVERE, null, ex );
			}

			Stash.serialize( cookie, COOKIE_PATH + getHost().replace( "/", "_" ) );
		}
	}
	
	/**
	 * Clears cookies
	 */
	public void cleanCookie() {
		try {
			Files.delete( Paths.get( COOKIE_PATH + getHost().replace( "/", "_" ) ) );
			
		} catch ( NoSuchFileException  e ) {

		} catch ( IOException e ) {
			logger.log(  Level.WARNING, null, e );
		}
	}
	
	/**
	 * Returns cookie header part
	 * @return
	 */
	protected String setCookieToHeader() {
		cookieFromStorage();
		String ret = cookie.stream().map( i -> i.get(  "name" ) + "=" + i.get(  "value" ) ).collect( Collectors.joining( ";" ) );
		
		return ret.length() > 0 ? "Cookie: " + ret + "\n" : "";
	}
	
	/**
	 * Adds cookies to internal (in-memory) storage
	 * @param c Cookie HashNMap
	 */
	protected void _cookieAdd( HashMap<String, String> c ) {
		String name = c.get( "name" );
		
		for( int i = 0, l = cookie.size(); i < l; i++ ) {
			if ( cookie.get( i ).getOrDefault( "name", "" ).equals( name ) ) {
				cookie.remove( i );
				
				break;
			}
		}
		
		cookie.add( c );
	}
	
	public String getProtocol() {
		return protocol;
	}
	
	public String dumpURL() {
		return String.format(
				"URL dump:%n" +
				"Protocol: %s%n" +
				"Host: %s%n" +
				"Path: %s%n" +
				"Query: %s%n" +
				"File: %s%n" +
				"Port: %s%n",
				getProtocol(),
				getHost(),
				getPath(),
				getQuery(),
				getFile(),
				getPort()
		);
	}
}