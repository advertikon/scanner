/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ua.com.advertikon.crawler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author max
 */
public class Test {
	public Test() {
		
	}
	
	static public void run() throws IOException {
		Path fileName = Paths.get( "/var/www/html/oc/admin/controller/extension/module/adk_mail.php" );
		
		if( ! Files.exists( fileName ) ) {
			throw new IOException( "File " + fileName + " does not exist" );
		}
		
		String content = new String( Files.readAllBytes( fileName ) )
//			.replaceAll( "/(Controller|Model)Extension/", "$1" );
			.replaceFirst( "(class\\s+(Controller|Model))(Extension)", "$1" );
		
		System.out.println( content.substring( 5150, 5300 ) );
	}
}
