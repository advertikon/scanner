/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.copart; 

import java.net.*;

import ua.com.advertikon.helper.*;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ua.com.advertikon.helper.*;

/**
 *
 * @author max
 */
public class Copart {
	
	
	private final static String SITE = "https://copart.com";
	private ASocket socket;
	private static final Logger logger = Logger.getLogger( Copart.class.getName() );
	private static final Level logLevel = Level.FINE;
	private final static String BASE = "files/";
	private String currentContent = "";
	private final Pattern linkPattern = Pattern.compile( "link|src=(\"|')(.*?)\\1" );
	private boolean isFirstRun = true;
	protected String firstJS = "";
	protected String xhrUrl = "";
	protected String iFrame = "https://content.incapsula.com/jsTest.html";
	protected String onloadJS = "";
	protected String externalJS = "";


    /**
     * @param args the command line arguments
	 * @throws java.io.IOException
	 * @throws ua.com.advertikon.helper.AException
     */
    public static void main(String[] args) throws IOException, AException {
		logger.getParent().setLevel( logLevel );
		logger.getParent().getHandlers()[ 0 ].setLevel( logLevel );

        new Copart().start();
    }
	
	protected void start() throws AException, IOException {
//		URL url;
//		try {
//			url = new URL( SITE + "/_Incapsula_Resource?SWJIYLWA=719d34d31c8e3a6e6fffd425f7e032f3" );
//			HttpURLConnection connection = ( HttpURLConnection ) url.openConnection();
//			BufferedReader reader = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
//			String line = "";
//			while ( null != ( line = reader.readLine() ) ) {
//				System.out.println( line );
//			}
//		} catch ( MalformedURLException ex ) {
//			Logger.getLogger( Copart.class.getName() ).log( Level.SEVERE, null, ex );
//		}
		
		
//		parceMainPage();
//		parseIFrame();
		parseExternalScript();
	}
	
	protected void parceMainPage() throws AException {
		String inlineScript = "";
		
		// Content of main page
		String mainPageContent = run( SITE );
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
		
		// Encodes inline script
		Pattern p1 = Pattern.compile( "var b=(\"|')(.*?)\\1" );
		Matcher m1 = p1.matcher(  mainPageContent );
		
		if ( m1.find() ) {
			inlineScript = decodeScript( m1.group( 2 ) );
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
	}
	
	protected void parseExternalScript() throws AException {
		String content = "";
//		firstJS = "/_Incapsula_Resource?SWJIYLWA=719d34d31c8e3a6e6fffd425f7e032f3";
//
//		if ( firstJS.isEmpty() ) {
//			throw new AException( "External JS script URl is missing" );
//		}
		
		// Content of main page
//		String rawContent = run( SITE + firstJS );
//		saveContent( rawContent, "externalJSRaw" );
		int c;
		
		String rawContent = "";
		FileInputStream r = null;
		try {
			r = new FileInputStream( new File( "files/externalJSRaw" ) );
		} catch (FileNotFoundException ex) {
			Logger.getLogger(Copart.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		try {
			while( -1 != ( c = r.read() ) ) {
				rawContent += (char)c;
			}
		} catch (IOException ex) {
			Logger.getLogger(Copart.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		// Encodes inline script
		Pattern p1 = Pattern.compile( "var b=(\"|')(.*?)\\1" );
		Matcher m1 = p1.matcher(  rawContent );
		
		if ( m1.find() ) {
			content = decodeScript( m1.group( 2 ) );
			saveContent( content, "external" );

		} else {
			throw new AException( "Failed to decode external JS" ); 
		}
	}
	
	protected void parseIFrame() {
		// Content of th eiFrame
		String rawContent = run( iFrame );
		saveContent( rawContent, "iframeRaw" );
	}
	
	public String run( String site ) {
		String ret = "";

		try {
			socket = new ASocket( site );
			
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
			Logger.getLogger( Copart.class.getName() ).log( Level.SEVERE, null, ex );

		} finally {
			socket.close();
		}
		
		return ret;
	}
	
	protected void crawl( String site, String base ) throws IOException {
		currentContent = run( site );
		Files.createDirectories( Paths.get( base ) );
//				System.out.println( "Save file " + );
		try ( BufferedWriter writer = new BufferedWriter( new FileWriter( base + makeFileName( site ) ) ) ) {
			writer.write( currentContent );
		}
		
		getLinks().stream().forEach( i -> {
			String newSite = i;

			try {
				if ( i.startsWith( "//" ) ) {
					URL url = new URL( site );
					newSite = url.getProtocol() + ":" + i;

				} else if ( i.startsWith( "/" ) ) {
					newSite = site + i;

				} else {
					newSite = site + "/" + i;
				}

				crawl( newSite, base + makeFileName( site ) + "/" );

			} catch ( IOException ex ) {
				Logger.getLogger( Copart.class.getName() ).log( Level.SEVERE, null, ex );
			}
		} );
	}
	
	protected ArrayList<String> getLinks() {
		ArrayList<String> ret = new ArrayList<>();
		Matcher matcher = linkPattern.matcher( currentContent );
		
		while ( matcher.find() ) {
			ret.add(  matcher.group( 2 ) );
		}
		
		return ret;
	}
	
	protected String makeFileName( String name ) {
		return name.replaceAll( "[\\s:/\\\\]", "_" );
	}
	
	protected String decodeScript( String s ) {
//		String s = "7472797B766172207868723B76617220743D6E6577204461746528292E67657454696D6528293B766172207374617475733D227374617274223B7661722074696D696E673D6E65772041727261792833293B77696E646F772E6F6E756E6C6F61643D66756E6374696F6E28297B74696D696E675B325D3D22723A222B286E6577204461746528292E67657454696D6528292D74293B646F63756D656E742E637265617465456C656D656E742822696D6722292E7372633D222F5F496E63617073756C615F5265736F757263653F4553324C555243543D363726743D373826643D222B656E636F6465555249436F6D706F6E656E74287374617475732B222028222B74696D696E672E6A6F696E28292B222922297D3B69662877696E646F772E584D4C4874747052657175657374297B7868723D6E657720584D4C48747470526571756573747D656C73657B7868723D6E657720416374697665584F626A65637428224D6963726F736F66742E584D4C4854545022297D7868722E6F6E726561647973746174656368616E67653D66756E6374696F6E28297B737769746368287868722E72656164795374617465297B6361736520303A7374617475733D6E6577204461746528292E67657454696D6528292D742B223A2072657175657374206E6F7420696E697469616C697A656420223B627265616B3B6361736520313A7374617475733D6E6577204461746528292E67657454696D6528292D742B223A2073657276657220636F6E6E656374696F6E2065737461626C6973686564223B627265616B3B6361736520323A7374617475733D6E6577204461746528292E67657454696D6528292D742B223A2072657175657374207265636569766564223B627265616B3B6361736520333A7374617475733D6E6577204461746528292E67657454696D6528292D742B223A2070726F63657373696E672072657175657374223B627265616B3B6361736520343A7374617475733D22636F6D706C657465223B74696D696E675B315D3D22633A222B286E6577204461746528292E67657454696D6528292D74293B6966287868722E7374617475733D3D323030297B706172656E742E6C6F636174696F6E2E72656C6F616428297D627265616B7D7D3B74696D696E675B305D3D22733A222B286E6577204461746528292E67657454696D6528292D74293B7868722E6F70656E2822474554222C222F5F496E63617073756C615F5265736F757263653F535748414E45444C3D383331343736363231373133323632333532352C31353937343934363933313035393538303935382C373333373836393630373234343038353430382C343531353235222C66616C7365293B7868722E73656E64286E756C6C297D63617463682863297B7374617475732B3D6E6577204461746528292E67657454696D6528292D742B2220696E6361705F6578633A20222B633B646F63756D656E742E637265617465456C656D656E742822696D6722292E7372633D222F5F496E63617073756C615F5265736F757263653F4553324C555243543D363726743D373826643D222B656E636F6465555249436F6D706F6E656E74287374617475732B222028222B74696D696E672E6A6F696E28292B222922297D3B";
//		String s = "766172205f3078666466373d5b275c7834645c783633";
//		System.out.println( s );

		ByteBuffer out = ByteBuffer.allocate( s.length() );
		
		for ( int i = 0, l = s.length(); i < l; i+= 2 ) {
//			System.out.print( String.format( "%4s-", Integer.parseInt( s.substring( i, i + 2 ), 16 ) ) );
//			System.out.print( String.format( "%4s", "0x" + s.substring( i, i + 2 ) ) );
			out.putShort( (short)Integer.parseInt( s.substring( i, i + 2 ), 16 ) );
		}
//		System.out.println( out.array() );
//		System.out.println(Arrays.toString(out.array()) );
		String code = new String( out.array(), Charset.forName( "UTF-16" ) );
		code = deHexCode( code );
		
		System.out.println( code );

		return code;
	}
	
	protected String deHexCode( String s ) {
		StringBuilder temp = new StringBuilder();
		int start = 0, index = 0;
		
		for( int i = 0, l = s.length(); i < l; i++ ) {
			index = s.indexOf( "\\x", index );

			if ( -1 != index ) {
				ByteBuffer b = ByteBuffer.allocate( 2 );
				b.putShort( (short)Integer.parseInt( s.substring( index + 2, index + 4 ), 16 ) );
				
//				System.out.println( String.format(
//						"First part: %s, second part: %s, first int :%d, second int: %d, first hex: %s, second hex: %s",
//						s.substring( index + 4, index + 6 ),
//						s.substring( index + 6, index + 8 ),
//						Integer.parseInt( s.substring( index + 4, index + 6 ), 16 ),
//						Integer.parseInt( s.substring( index + 6, index + 8 ), 16 ),
//						Hex
//				) );
//				String insert = Integer.parseInt( firstHex + secondHex, 16	) + "";

				temp.append( s.substring( start, index) ).append( new String( b.array(), Charset.forName( "UTF-16" ) ) );
				index += 4;
				i += 3;
				start = index;

			} else {
				temp.append( s.substring( start, s.length() ) );
				break;
			}
		}
		
		return temp.toString();
	}
	
	protected void saveContent( String s, String name ) {
		try {
			Files.createDirectories( Paths.get( BASE ) );
		} catch ( IOException ex ) {
			Logger.getLogger( Copart.class.getName() ).log( Level.SEVERE, null, ex );
		}

		try ( BufferedWriter writer = new BufferedWriter( new FileWriter( BASE + makeFileName( name ) ) ) ) {
			writer.write( s );
		} catch ( IOException ex ) {
			Logger.getLogger( Copart.class.getName() ).log( Level.SEVERE, null, ex );
		}
	}
	
}
