package ua.com.advertikon.console;

import ua.com.advertikon.helper.*;

import java.util.*;

public class ConsoleModel {
	final String URL = "https://shop.advertikon.com.ua/support/ticket_button.php";

	public List<HashMap<String, String>> getInstallation() {
		List<HashMap<String, String>> ret = new ArrayList<>();
		String data = AUrl.get( URL + "?installation=true" );
		String[] lines = data.split( "\n" );
		Country c = new Country();

		for ( String line1 : lines ) {
			String[] line = line1.split( "\t" );
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