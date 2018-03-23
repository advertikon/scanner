/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.copart; 

import java.net.*;

import ua.com.advertikon.helper.*;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import ua.com.advertikon.*;

/**
 *
 * @author max
 */
public class Copart {
	
	
	final String site = "http://oc.ua";
	final int port = 80;
	private static final Logger L = Logger.getLogger( Copart.class.getName() );

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		Logger.getGlobal().setLevel( Level.INFO );
        new Copart().run();
    }
	
	public void run() {
		try {
			System.out.println( Connection.socket( new URL( site ), port ) );

		} catch ( MalformedURLException e ) {
			L.log( Level.SEVERE, null, e );
		}
	}
}
