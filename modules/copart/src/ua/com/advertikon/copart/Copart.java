/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.copart;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlOption;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSelect;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.apache.http.client.CredentialsProvider;

import org.apache.commons.logging.Log; 
import org.apache.commons.logging.LogFactory; 

import java.net.*;

import java.io.*;
import java.util.List;
import java.util.Set;

/**
 *
 * @author max
 */
public class Copart {
	
	private Log log = null;
	final String site = "https://auction.copart.com/c3/auctions.html?appId=G2&siteLanguage=en&appId=g2#24-A";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		System.getProperties().put("org.apache.commons.logging.simplelog.defaultlog", "trace");
		
        new Copart().run();
    }
	
	public void run() {
		log = LogFactory.getLog(Copart.class);
		log.debug( "text" );
		
		try {
            Class.forName( "com.gargoylesoftware.htmlunit.WebClient" );
        } catch ( ClassNotFoundException e ) {
                
        }
        try {
                final WebClient webClient = new WebClient();
				System.out.println( webClient.getBrowserVersion() );
				System.out.println( "Cookie: " + webClient.getCookieManager().isCookiesEnabled() );
				webClient.getOptions().setCssEnabled( false );
				
				final HtmlPage page = webClient.getPage( site );
				
				Set<Cookie> l = webClient.getCookies( new URL( site ) );
				System.out.println( l );

				
				System.out.println( page.getTitleText() );

        } catch ( IOException e ) {
			System.err.println( e );
        }
	}
    
}
