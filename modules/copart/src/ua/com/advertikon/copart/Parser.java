/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.copart;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ua.com.advertikon.helper.AException;
import ua.com.advertikon.helper.ASocket;
import ua.com.advertikon.helper.Stash;

/**
 *
 * @author max
 */
public class Parser {
	private ASocket socket = new ASocket();
	private static final Logger logger = Logger.getLogger( Parser.class.getName() );
	private boolean isFirstRun = true;
	private final static String SITE = "https://copart.com";
	protected String firstJS = "";
	protected String xhrUrl = "";
	protected String iFrame = "https://content.incapsula.com/jsTest.html";
	protected String onloadJS = "";
	protected String externalJS = "";
	private final Pattern linkPattern = Pattern.compile( "link|src=(\"|')(.*?)\\1" );
	private String currentContent = "";
	private final static String BASE = "files/";
	protected Session session;
	
	public String query( String page ) throws AException, IOException {
		socket.url( page );
		socket.run();
		String content = socket.getBody();
		
		if ( isBlocked( content ) ) {
			parseMainPage( content );
		}
		
		socket.url( page );
		socket.run();
		return socket.getBody();
	}

	/**
	 * Fetches page
	 * @param site URL of target page
	 * @return
	 */
	public String fetchPage( String site ) {
		String ret = "";

		try {
			socket.url( site );
			
			if ( isFirstRun ) {
				socket.cleanCookie();
				isFirstRun = false;
			}

			socket.run();

			if ( socket.getResponceCode() == 200 ) {
				ret = socket.getBody();

			} else {
				logger.log(  Level.SEVERE, String.format( "Bad responce: %s [%d]", socket.getResponceMessage(), socket.getResponceCode() ) );
			}

		} catch ( IOException ex ) {
			logger.log( Level.SEVERE, null, ex );

		} catch ( AException ex ) {
			logger.log( Level.SEVERE, null, ex );

		} finally {
			socket.close();
		}
		
		return ret;
	}
	
	/**
	 * Parser Incapsula main page and saves links and scripts to corresponding variables and files
	 * @throws AException
	 */
	public void parseMainPage( String mainPageContent ) throws AException, IOException {
		String inlineScript = "";
		
		// Content of main page
//		String mainPageContent = fetchPage( SITE );
		saveContent( mainPageContent, "mainPageRaw" );

		// First external JS
		Pattern p = Pattern.compile( "<script\\s+src=(\"|')(.*?)\\1" );
		Matcher m = p.matcher(  mainPageContent );
		
		if ( m.find() ) {
			firstJS = m.group( 2 );

		} else {
			throw new AException( "External JS link not found" );
		}
		
		System.out.println( "Exteral script: " + firstJS );
		
		externalJS = fetchPage( socket.getProtocol() + "://" + socket.getHost() + firstJS );
		Stash.filePutContents( BASE + "externalJS", externalJS );
		parseExternalScript();
		session = new Session( socket );
		session.setIncapsulaCookie();
		pushIncapsulaCookie();
		
		
		// Encodes inline script
		Pattern p1 = Pattern.compile( "var b=(\"|')(.*?)\\1" );
		Matcher m1 = p1.matcher(  mainPageContent );
		
		if ( m1.find() ) {
			inlineScript = Decoder.decodeScript( m1.group( 2 ) );
			saveContent( inlineScript, "inline" );

		} else {
			throw new AException( "Inline script wasn't decoded" );
		}

		// xhr request url from inline script
		Pattern p2 = Pattern.compile( "xhr.open\\(\"GET\",(\"|')(.*?)\\1" );
		Matcher m2 = p2.matcher(  inlineScript );
		
		if ( m2.find() ) {
			xhrUrl = m2.group( 2 );

		} else {
			throw new AException( "xhr url is nor found" );
		}
		
		sendXHR();
		
		System.out.println( "xhrUrl: " + xhrUrl );
		
		// Image url to load on page laod
		Pattern p3 = Pattern.compile( "document.createElement\\(\"img\"\\).src=(\"|')(.*?)\\1" );
		Matcher m3 = p3.matcher(  inlineScript );
		
		if ( m3.find() ) {
			onloadJS = m3.group( 2 );
		} else {
			throw new AException( "Image url is not found" );
		}
		
		System.out.println( "Inage: " + onloadJS );
		
		sendLastRequest();
	}
	
	/**
	 * Parses external script from file and saves decoded version to file
	 * @throws AException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	protected void parseExternalScript() throws AException, FileNotFoundException, IOException {		
		// Encodes inline script
		Pattern p1 = Pattern.compile( "var b=(\"|')(.*?)\\1" );
		Matcher m1 = p1.matcher( externalJS );
		
		if ( m1.find() ) {
			externalJS = Decoder.decodeScript( m1.group( 2 ) );
			saveContent( externalJS, "external" );

		} else {
			throw new AException( "Failed to decode external JS" ); 
		}
	}
	
	protected void parseIFrame() throws IOException {
		// Content of th eiFrame
		String rawContent = fetchPage( iFrame );
		saveContent( rawContent, "iframeRaw" );
	}
	
	/**
	 * Returns list of all the links in current content buffer
	 * @return
	 */
	protected ArrayList<String> getLinks() {
		ArrayList<String> ret = new ArrayList<>();
		Matcher matcher = linkPattern.matcher( currentContent );
		
		while ( matcher.find() ) {
			ret.add(  matcher.group( 2 ) );
		}
		
		return ret;
	}
	
	/**
	 * Saves content into file
	 * @param s Content to be saved
	 * @param name File name
	 * @throws IOException
	 */
	protected void saveContent( String s, String name ) throws IOException {
		Files.createDirectories( Paths.get( BASE ) );

		try ( BufferedWriter writer = new BufferedWriter( new FileWriter( BASE + makeFileName( name ) ) ) ) {
			writer.write( s );
		}
	}
	
	/**
	 * Dectyptes crypted strings at externalPretty file and saves content to externalPretty.copy file
	 */
	protected void deobfuscateNames() {
		StringBuilder in = new StringBuilder();
		int c;
		String content;
		
		try ( BufferedReader reader = new BufferedReader( new FileReader( BASE + "externalPretty" ) ) ) {
			while( -1 != ( c = reader.read() ) ) {
				in.append( (char) c );
			}

		} catch (IOException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
		
		content = Decoder.decodeNames( in.toString() );
		
		try ( BufferedWriter writer = new BufferedWriter( new FileWriter( BASE + "externalPretty.copy" ) ) ) {
			writer.write( content );

		} catch ( IOException ex ) {
			logger.log( Level.SEVERE, null, ex );
		}
	}
	
	protected String makeFileName( String name ) {
		return name.replaceAll( "[\\s:/\\\\]", "_" );
	}
	
	protected void pushIncapsulaCookie() {
		fetchPage( socket.getProtocol() + "://" + socket.getHost() + "/_Incapsula_Resource?SWKMTFSR=1&e=" + Math.random() );
	}
	
	protected void sendXHR() {
		fetchPage( socket.getProtocol() + "://" + socket.getHost() + xhrUrl );
	}
	
	protected void sendLastRequest() {
		StringBuilder query = new StringBuilder();
		query.append( "s=" ).append( Math.round( Math.random() * 10 ) ).append( ",," ).append( "r=" ).append( Math.round( Math.random() * 1000 ) );
		fetchPage( socket.getProtocol() + "://" + socket.getHost() + onloadJS + query.toString() );
	}
	
	protected boolean isBlocked( String page ) {
		return true;
	}
}
