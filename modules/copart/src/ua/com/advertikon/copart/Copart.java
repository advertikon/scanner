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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
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
	protected String[] codes;


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
//		parseExternalScript();

		initCodes();
		System.out.println( decode( "0x0", "Q]p1") );
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
	
	protected void initCodes() {
		String[] c = {"c8KYNhNDaQ==", "wonDj8K+NTY8TzhFDg==", "DQTDqQ==", "w6pCAz8=", "w416RcOMBFx2", "w7rCq8Or", "C3sNw6fChxLDig==", "WsKsw5w=", "wo/Cq8Om", "b8Knw5DCt8O5wqnClcKow65f", "w67DqMOzwplCCF7CrMKWWQc=", "UwJdbsKlwow=", "w4HCicO0", "w4gkVQ==", "wofCjMK9", "UEha", "Z8KEwp/DisKjE8OEwqc=", "HsOzwpA=", "ZwBL", "w5ppw5o=", "I1fDkQ==", "w6ptw6w=", "w6t1Gw==", "w4TCqgQ=", "ecKgw4E=", "woLDgsKQ", "JcOmwpbCrw1P", "GcK3wr4=", "w6TCthE=", "w4Z4w7rCjH3DrCROD8OWw5s=", "w7DCvxfCvxDChsKBSw==", "D3EXw4DCmg3DmiMmGMOQ", 
		"w77DosOWwqFxPE/CrA==", "TXMw", "RyLDtQ==", "RipD", "Q2PDgg==", "wpUYwq9u", "w7wgL8Kqwrsoew==", "QHjDrg==", "wqbCpio=", "aTjDvsO2wq7Djw==", "cyRC", "csK/Gw==", "wqQXwr9uw6AOwp1PGw==", "EMKRw5nDrsOFw74=", "wrlJw6U=", "wq3DqsK5", "bsKQwrrCksKU", "DTbDgMOBSDw=", "w5Ndcg==", "wozCqsKi", "wpNFFjbDnUU=", "CMOYNyMbSQ==", "HsOnwqo=", "wrkJwpM=", "QcKSw5bDpcOCw7M=", "wpLCu8KGwrvDtw==", "Fl7Dtg==", "RHnDhw==", "wp/DicK7IhM6RThg", "HsKEw5Y=", "CSPDpg==", "fE/DisOKZkfDvyzCtA4=", "w5ZXPA==", "Q3jDmw==", "wrXCvcO+", 
		"wph5w6HCk2U=", "aUTDtcO1dQA=", "woXCpxg=", "XcK4OQ==", "Undg", "cgDDmMOwVTTCvsOS", "OMOiw5vCqMO9wqg=", "KsOOVQ==", "wqnCuRTCpBnCjsKQGW3CqsO1wrV3wr/DjcOn", "w7bDosOawrNiMw==", "woDCn8Kr", "TRBd", "w4xfw6c=", "OcOvwo3CrxBJwr8CGMKzZVHDuMKFNwrDtg==", "w6nCscKEwqvDisKlw4RBwqQ=", "wpLCkjVHTQ==", "dylB", "wo3ChzdJTcKk", "cit3", "UcKnw6A=", "dgPDvsOjTjPCj8OQwr0/Ow==", "w7lXw5/Cqm8bWQ==", "dlvDrA==", "H8Opwoo=", "H8KBwps=", "LMOIwprDjMK+FcKmw61IwqDDjcK2TcK4wo8tM8KIUsOlwpE=", "wp3Dr8Kv", "DMKYw4LDrsOYw7jCmcOSTEPCrMKrGx8FEELCg3nDhsO3EMK5w6XCg8Oh", 
		"CD3DisODRBvCoA==", "w6Vew4TCqnId", "wpEwfcOtI8O+wqZmT2zDu8Oaw6XCisKMwqXCpXQRwo3DrifDpy5NwqJNAMOFwqfCvhFVwpLCg8K/", "CVPCsQ==", "QAFHYsKlwofDlg==", "wobDgsKxIAE7", "wpvCgMK+cCtnbcOswrI=", "w41vXcOxGQ==", "w79Dw50=", "DcOGZy/ClmfCsTsF", "SHgUBMOCw5dlR8OuT8OPwq3ChWcow7R/wqjDqMKtwrM=", "w5PDrMOY", "wq7CvcOawr7Cjw==", "QnQWBsOYw54=", "w51TGzPDmg==", "CMOHcw==", "SwTDpA==", "KFTDgMOLe27DsA==", "w6rDssOHwrw=", "wpckfw==", "w5sed8OQw5YdcA==", "wqFlw4LCvnrCvBPCo3Y=", "QMKJAMOxE8OR", "Tj3DqQ==", "LMOIwprDjMK+FcKmw61IwqDDjcKoTsOtwpgoKMKcYMOiwr/CtsK2wq3DonFHw4hoDcKUw6nDhxc=", 
		"w45zRMO/BF9g", "AsOUNSEBQMOPw5A=", "QH5YBMOUw4JtTMK6T8OTw6M=", "LMOvOw==", "w6hgcQ==", "w4vDv2HCiShBRkI8BMKD", "wr0uwpw=", "QMKWwoUp", "wo/Cgy9HXsKtK8K4wp4=", "CWYQw4DCmgw=", "RMKYwpouPEfCmWvDiDTDjFrDtcKIwoAa", "w50TbsOCw5o=", "wo89fsOjLcOxwo1sRTbCp8ODw7vCoMKIwrDCjw==", "w6pbwqRWwpU=", "w4fCncO7ZXh6KcO/w7HDjsOdwpnCjsKPJsOmw7U5LVrChMKiwrNnccOpwq8=", "WHAUFMOJ", "w4BCATPDiUHCp8OgwpbDozYLa2TDgw/CpD0=", "OHbCrsK5Og==", "An8Pw5rCiR7Dmy8gWcOVwpJTwogweggmPw==", "w60kN8KLwrc=", "fCzDtsOxwrzDkyjCoQ==", "b8KRLRNebyVmwp5vQRcNwrtTw4Ji", 
		"TSPDo8OswqzDmQLCg0DDq8KWbiI=", "w7tEIT5WMQ==", "w6lZKiZLNsO3wq7DlQ==", "YFnDssOhdRs=", "cWEQAMOCw4JnTw==", "w4zChMOkf2to", "w4HCvsOPOsONdMK6BSbDjxQ=", "NsKcwqHClMKFag==", "w4rClMO/Y3J+", "w7HCohzCuQPCkg==", "w44ZA8KQIsKH", "LkrDgcOcYg==", "McO/w5zCvMO9wrM=", "w43Cr8OR", "RcOEw6dzw6nDgg==", "w4jDsnLCjzNG", "wr3CsMOYw4PCiF0=", "MsKTwq3ClMKedMKwOjE=", "LMORwonDjcK+FcK9w61Gwr3Ck8K9", "wrFzw4/CqGjCpg==", "w4FjJiRFKsOWwpHDuGrCqA==", "woTCmjBdTcK/", "PgDDi8OKWTrCr8Ozw7VFJDNMw6MEwozDhgjCiMKwwpZi", "woHCosKDwr3DpsKq", 
		"SEDDilpbwqDClhEVe8OQw77DqcK/UcKrNMOnagJkAsK5csOWw4tFE0PDhA==", "T8KBwoU0L1U=", "w7pTKzhPJ8OMwojCtzzCrsKIw67Dgm3CgsOFfcOvw57DjMOFw6nClmg7wohWPsO4w4vCsiTCgcO/woDCnw==", "OcOcwobDmMKjCA==", "wqjCusOew5PCmV1yw5kwSgrDuwzDpCU=", "w4fCp8OKJcOpbw==", "w7UkLcKXwrUkaiTCncK9wqMBw53CvcOdwrrCtgw=", "w7rCuwPCoxDCgMKQVnrDssO7wqphwq7DjA==", "wo/Dn8K2NAEg", "wo89fsOjLcOxwo1sRTbCpcOcw6XCgMKMwr7Cnntewoo=", "EsKVw4HDoMOWw7fCssOYRhnDvcKpFBYeHlHDhiHCjcKxP8Or", "w5TCvsOPI8O4", "E8OUNyAAVsKMw5otQsKZwofDusKxeGM3G0km", "cA7Dp8OxQg==", 
		"ckjDtcO2bh/Dt2sPcFkPw6vDlMOLM8OUwo7DnivCvg==", "IsOmw5nCusOs", "wq/CocOfw5TCk1kvwqAjTT/DhDfDriXDiMOfw6nDolXDmMOjCMKpw6kdFMOE", "GMK8w5AhwpjCpWNWw7DCuMKGTcOkw5bDpG9bw6zCnTjClg==", "c0DDt8OnZA==", "OcOSwo7Dh8O5D8KWw5tEwqbCmcK2RcKww5ZvKsKKVcOswpbDvg==", "wpzDhsKzMhA=", "w5PCnmhSDcKwb8Krw5krKA==", "JMKuw4Q=", "EMOhw5k=", "w4vCiMOibQ==", "w5UMAsKV", "w7cqPA==", "w6FKdg==", "MMKWwq3ChsKFfMKcIzknwofChMK2", "w4x6w7M=", "D8KGw5Q=", "wpfCiMKv", "ZsOcwrHCphpGwpAUFcKrYWDDnsKJKwvDsGfCsFgPHSl/W8K/wrTDjsKOOMOEZ8KKKg==", 
		"VjlAwq9yJQ==", "wojCjcKt", "wpTCr8KZwqY=", "ScKBwoAoPA==", "CsKVw5vDvMOU", "w58jNw==", "wqU6ZA==", "bzLDssOkwq7DmR/CoEfDrMKWYyI=", "J3rCpQ==", "w4QKDg==", "Vwpr", "wpgnJMKaJMKeNsK5w5rCosOuEkEpBEZAQwPDqSbDtWhMURTCviw9HcO4FAbCpw==", "w74OKQ==", "AXsKw4DCjxjDig==", "w6VUw40=", "FjrDgMOCUyM=", "VAJRcMKhwozDizI=", "RGxT", "N8Oow5vCvMOmwqzCgg==", "w5fCscOHM8O7dcK1DjY=", "H8Kbw5nDusOew7rCow==", "YC/DsA==", "w4BCATPDiUHCp8OgwpY=", "w5txUsO3CVRGw7DCl19Ge2zClBxGScO0", "w5hbwrxG", "w73DosOAwoB/Nk8=", "TcOUNDbDhHLDrSJXw47CmsOdZsKdwoM=", 
		"wqIJwrdiw7I=", "wo05ZsOtPsO4", "w7dLbw==", "UsOBaDhXXcKRw4lvSsOIwonCmMKkLGtt", "XWEUCMOY", "UwVTd8KNwp0=", "wqZSw78=", "ZyTDug==", "UXfDtQ==", "GsOwwpk=", "KMKFw50=", "H8Olwr8=", "fMKVOQZ2dQ==", "GcKpw54=", "w7nDr8OVwqZXLw==", "PcOvwp4=", "B8KywqY=", "MMKMwqnClcKwbQ==", "S8KrNg==", "UXxy", "w41LFijDr1Q=", "aTrDug==", "H8K7w5ImwrTCtA==", "I3w8", "F8OCwow=", "w5JdaQ==", "wrFxw4s=", "w5BXwpk=", "CcO0LQ==", "YMKBPw==", "w6QXBQ==", "M8KWw7I=", "BcKdw7Q=", "aDV/", "LMOyZA==", "w5huwqE=", "w7/Dh30=", "N8Ovw5TCvcOKwq/Cg8Kow5wW", 
		"wr7CpcOXwqXCuMK2wpNYw5XCsQ==", "UwVTd8KPwobDgSNOw4E=", "HcOmwqzDr8KSPcK+w4B5wp7Cu8KUb8OWwrARF8K9aMOfwrfDgMKIwobCm15Iw45uD8KYw6HDhRvCohPCusOsw6vDnkbCgml/w5hOQBDDnFBnRXXCp8Kmw71sw7hUw7BFw5Z8Ow==", "WWbDug==", "D8O/Ew==", "ScOUMzbDg3LDrSJUw47Cng==", "RsK4HWFq", "Z8KtFk1mwqE=", "w7twacOoKSfCgMKhw6TDtsKVw78Iwoc=", "bcKeHMO3Hg==", "wpHChMOX", "w7JZJipWKg==", "XWQaEsOYw4Q=", "w4xCfsKyOAbChQ==", "dRrDqcO3Uy8=", "HsK0w7g=", "w5LDvXDCizlgfw==", "XAhcYsK4woE=", "w4MdHsKA", "w4lyw7rCmH3Dtg==", "w41MGDHDh0U=", 
		"dFVPw5Y8", "w6R+w7E=", "eMKwwoTDuUhbw5cbUcO3fA7DsMOdahjCs2nDpkECMkdILsKXw4HDocOsecOB", "dR/Dp8OtUw==", "DMK8w4M=", "w4V/wq8=", "wpIpUA==", "w4dUCg==", "G8OAYjjCs2HCuzsgw4Y=", "w41Ow5A=", "w5F4w4fCi3vDtz9K", "eMKvMA==", "aQVw", "ezxx", "wq/Ci8Kp", "wpoowpg=", "aEDDvQ==", "LTjDqA==", "SD1AwqxpIA==", "F8OIOzcbUw==", "wrjCvhzCrRLCksKQBA==", "GcK7TA==", "w592Sg==", "WTDDlMOxUzDCpsOW", "bADDosOq", "CMOxworCqQA=", "QErDh0hCwq0=", "w5Viw6fClw==", "w5XDg8O9", "w5ICOQ==", "OsO2wqA=", "InLCrMKrK8K4", "wqxqw4w=", 
		"WWgR", "w5kfLQ==", "wqYSwq8=", "wpZSNsKPWAAsT8KcR8KHN8KDccK8FgJ4cCvChgfCmcO8w656w4rDkyt+WjUtwofDim/CtFIyw6c=", "wp/CjMOC"};
		ArrayDeque temp = new ArrayDeque( Arrays.asList( c ) );

		for (int s = 215; --s > 0;){
		  temp.push( temp.pollFirst() );
		}
		
		codes = (String[]) temp.toArray( new String[ temp.size() ] );
	}
	
	protected String decode( String n, String fn ) {
		int num = Integer.decode( n );
		
		System.out.println( String.format( "%s - %d = %s", n, num, codes[ num ] ) );
		
		String data = codes[ num ];

		int y = 0;
		int temp;
		StringBuilder tempData = new StringBuilder();

		data = Base64.getEncoder().encodeToString( data.getBytes() );
    
		int val = 0;

		// for ( $ii = 0; $ii < strlen( $data ); $ii++ ) {
		// 	echo dechex( ord( $data[ $ii ] ) ) . '-';
		// }
		// echo '<br>';
		int key = data.length();
		for (; val < key; val++) {
		  /** @type {string} */
		  // $tempData = $tempData + ("%" + ("00" + $data["charCodeAt"](val)["toString"](16))["slice"](-2));
		  // echo sprintf("%%%s", dechex( ord( $data[ $val ] ) ) ) . '-' . mb_substr( $data, $val, 1 ) . '<br>';
		  tempData.append( Integer.toHexString( data.codePointAt( val ) ) );
		}

	data = tempData.toString();

	HashMap<Integer, Integer> secretKey = new HashMap<>();

	for ( int x = 0; x < 256; x++) {
	  secretKey.put( x, x );
	}
	/** @type {number} */
	for( int x = 0; x < 256; x++ ) {
	  /** @type {number} */
	  // y = (y + secretKey[x] + fn["charCodeAt"](x % fn["length"])) % 256;
	  y += ( secretKey.get( x ) + fn.codePointAt( x % fn.length() ) ) % 256;
	  temp = secretKey.get( x );
	  secretKey.replace( x, secretKey.get( y ) );
	  secretKey.replace( y, temp );
	}

	int x = 0;

	y = 0;

	int i = 0;
	ByteBuffer testResult = ByteBuffer.allocate( data.length() );

	System.out.println( data );
	for (; i < data.length(); i++) {
	  x = ( x + 1 ) % 256;
	  y = ( y + secretKey.get( x ) ) % 256;

	  temp = secretKey.get( x );
	  secretKey.replace( x, secretKey.get( y ) );
	  secretKey.replace( y, temp );
	  
	  System.out.println( x );
	  // $testResult = $testResult + String["fromCharCode"](data["charCodeAt"](i) ^ secretKey[(secretKey[x] + secretKey[y]) % 256]);

	 // testResult[] = chr( ord( mb_substr( $data, $i, 1 ) ) ^ $secretKey[ ( $secretKey[ $x ] + $secretKey[ $y ] ) % 256 ] );
	 int currentCode = data.codePointAt( i );
	 int sx = secretKey.get( x );
	 int sy = secretKey.get( y );
	 int mask = secretKey.get( ( sx + sy ) % 256 );
	 char c = (char)( currentCode ^ mask );
	  testResult.putChar( c );

//	  mb_internal_encoding( 'utf-8' );
//	  $char =  mb_substr( mb_convert_encoding( $data, 'utf-8' ), $i, 1 );
//	  echo ord( $char ) . ' - ' .
//		  ( $secretKey[ ( $secretKey[ $x ] + $secretKey[ $y ] ) % 256 ] ) . ' - ' . 
//		  ( ord( $char ) ^ $secretKey[ ( $secretKey[ $x ] + $secretKey[ $y ] ) % 256 ] ) . ' - ' . 
//		  chr ( ord( $char ) ^ $secretKey[ ( $secretKey[ $x ] + $secretKey[ $y ] ) % 256 ] ) . '<br>';
//	  mb_internal_encoding( 'utf-8' );
	}

      return new String( testResult.array() );
	}
	
}
