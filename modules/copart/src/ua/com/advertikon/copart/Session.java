/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.copart;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import ua.com.advertikon.helper.ASocket;
import ua.com.advertikon.helper.AUrl;

/**
 *
 * @author max
 */
public class Session {
	protected String[] browserInfo = {
		 "navigator%3Dtrue"
		, "navigator.vendor%3DGoogle%20Inc."
		, "navigator.appName%3DNetscape"
		, "navigator.plugins.length%3D%3D0%3Dfalse"
		, "navigator.platform%3DLinux%20x86_64"
		, "navigator.webdriver%3Dundefined"
		, "plugin_ext%3Dno%20extention"
		, "plugin_ext%3Dso"
		, "ActiveXObject%3Dfalse"
		, "webkitURL%3Dtrue"
		, "_phantom%3Dfalse"
		, "callPhantom%3Dfalse"
		, "chrome%3Dtrue"
		, "yandex%3Dfalse"
		, "opera%3Dfalse"
		, "opr%3Dfalse"
		, "safari%3Dfalse"
		, "awesomium%3Dfalse"
		, "puffinDevice%3Dfalse"
		, "__nightmare%3Dfalse"
		, "_Selenium_IDE_Recorder%3Dfalse"
		, "document.__webdriver_script_fn%3Dfalse"
		, "document.%24cdc_asdjflasutopfhvcZLmcfl_%3Dfalse"
		, "process.version%3Dfalse"
		, "navigator.cpuClass%3Dfalse"
		, "navigator.oscpu%3Dfalse"
		, "navigator.connection%3Dtrue"
		, "navigator.language%3D%3D'C'%3Dfalse"
		, "window.outerWidth%3D%3D0%3Dfalse"
		, "window.outerHeight%3D%3D0%3Dfalse"
		, "window.WebGLRenderingContext%3Dtrue"
		, "document.documentMode%3Dundefined"
		, "eval.toString().length%3D33"
	};
	
	protected ASocket socket;
	
	public Session( ASocket s ) {
		socket = s;
	}
	
	protected String[] getIncapsulaCookies() {
//		ArrayList<String> tmp = new ArrayList<>();
//
//		for( HashMap<String, String> c: getCookies() ) {
//			if( c.get(  "name" ).startsWith( "incap_ses_" ) ) {
//				tmp.add( c.get( "value") );
//			}
//		}
//		String[] t = new String[ 10 ];
		return getCookies().stream().filter( i -> i.getOrDefault(  "name", "" ).startsWith( "incap_ses_" ) ).map( i -> i.get( "value" ) ).toArray(String[]::new);
	}
	
	protected ArrayList<HashMap<String, String>> getCookies() {
		return socket.getCookies();
	}
	
	public void setIncapsulaCookie() {
		String[] cookies = getIncapsulaCookies();
		int[] hashes = new int[ cookies.length ];
		String salt = AUrl.implode( ",", browserInfo );
		final String chain = "OL7NYH4sHh8asS+HPmdAcHtTwyia7a/5oFl/FA==";

		for ( int i = 0; i < cookies.length; i++ ) {
			hashes[ i ] = calculateStringHash( salt + cookies[ i ] );
		}

		String stringOfHashes = AUrl.implode( ",", hashes );
		StringBuilder hexString = new StringBuilder();

		for ( int i = 0; i < chain.length(); i++ ) {
			hexString.append( Integer.toHexString( chain.codePointAt( i ) + stringOfHashes.codePointAt( i % stringOfHashes.length() ) ) );
		}

		String base64hash = Base64.getEncoder().encodeToString( ( Decoder.decrypt( Base64.getEncoder().encodeToString( salt.getBytes() ), chain.substring( 0, 5 ) ) + stringOfHashes + hexString ).getBytes() );
		addCookie( "___utmvc", base64hash );
	}
	
	protected int calculateStringHash( String input ) {
		int sum = 0;

		for ( int i = 0; i < input.length(); i++ ) {
			sum += input.codePointAt( i );
		}
		
		return sum;
	}
	
	protected void addCookie( String name, String value ) {
		HashMap<String, String> cookie = new HashMap<>();
		
		cookie.put( name, value );
		socket.addCookie( cookie );
		socket.cookieToStorage();
	}
}
