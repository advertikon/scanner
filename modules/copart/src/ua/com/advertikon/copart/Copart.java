/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.copart; 

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ua.com.advertikon.helper.AException;
import ua.com.advertikon.helper.ASocket;

/**
 *
 * @author max
 */
public class Copart {
	private final static String SITE = "https://copart.com";
	private static final Logger logger = Logger.getLogger( Copart.class.getName() );
	private static final Level logLevel = Level.FINE;

    /**
     * @param args the command line arguments
	 * @throws java.io.IOException
	 * @throws ua.com.advertikon.helper.AException
     */
    public static void main(String[] args) throws IOException, AException {
		logger.getParent().setLevel( logLevel );
		logger.getParent().getHandlers()[ 0 ].setLevel( logLevel );

        new Copart().query( SITE );
    }
	
	protected void start() throws AException, IOException {
//		parceMainPage();
//		parseIFrame();
//		parseExternalScript();

//		initCodes();
//		Log.dump( codes );
//		System.exit( 0);
//		decode( "0x142", "%QnW" );
//		deobfuscateNames();
	}
	
	public void query( String page ) throws AException, IOException {
		Parser parser = new Parser();
		System.out.println( parser.query( page ) );
	}
}
