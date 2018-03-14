package com.ua.advertikon.console;

import com.ua.advertikon.helper.*;

import java.time.*;
import java.util.*;

public class ConsoleModel {
	final String URL = "https://shop.advertikon.com.ua/support/ticket_button.php";

	public List<HashMap<String, String>> getInstallation() {
		List<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();
		String data = new AUrl().get( URL + "?installation=true" );
		String[] lines = data.split( "\f" );
		Country c = new Country();

		for ( int i = 0; i < lines.length; i++ ) {
			String[] line = lines[ i ].split( "\t" );
			HashMap<String, String> retLine = new HashMap<>();

			retLine.put( "id",            line[ 0 ] );
			retLine.put( "name",          line[ 1 ] );
			retLine.put( "code",          line[ 2 ] );
			retLine.put( "version",       line[ 3 ] );
			retLine.put( "oc_version",    line[ 4 ] );
			retLine.put( "date_created",  line[ 5 ] );
			retLine.put( "date_modified", line[ 6 ] );
			retLine.put( "country",       c.getNameByCode( line[ 7 ] ) );
			retLine.put( "fraud",         line[ 8 ] );
			retLine.put( "active",        line[ 9 ] );
			retLine.put( "localhost",     line[ 10 ] );

			ret.add( retLine );
		}

		return ret;
	}
}