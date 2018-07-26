package ua.com.advertikon.scanner;

import ua.com.advertikon.helper.*;

import java.sql.SQLException;
import java.sql.PreparedStatement;

import java.util.*;

/**
 *
 * @author max
 */
public class scannerDB extends DBhelper {
	final String MODULES_TABLE = "modules";

	scannerDB( ) {
		super();

		try{
			exec( "create table if not exists " + MODULES_TABLE + " (id integer, name text, price decimal, date_added text, date_modified text, sales integer, date text )" );
			Log.debug( "Creating table if needed" );

		} catch ( SQLException e ) {
			Log.exit( "scanner_db::constructor: " + e );
		}
	}

	public int getLastId() {
		String id = "0";

		try {
			for ( Map<String, String> row: query( "SELECT MAX(id) as max from " + MODULES_TABLE ) ) {
				id = row.get( "id" );
			}
			
		} catch ( SQLException e ) {
			Log.exit( "scanner_db::getLastId: " + e.getMessage() );
		}

		return id != null ? Integer.parseInt( id ) : 0;
	}

	public Iterator<Map<String, String>> getWorthModules() {
		List<Map<String, String>> ret = null;

		try {
			// More than 5000 sales/downloads or more than 500$ per month
			if ( mode.equals( "sqlite" ) ) {
				ret = query( "SELECT id FROM " + MODULES_TABLE + " WHERE date > date( 'now', '-1 month' ) AND ( price > 0 OR sales > 10000 ) GROUP BY id HAVING ( ( MAX( sales ) - MIN( sales ) ) * price ) * 30 / ( MAX( julianday( date ) ) - MIN( julianday( date ) ) )  > 200" );
				
			} else {
				ret = query( "SELECT id FROM " + MODULES_TABLE + " WHERE date > DATE_SUB( NOW(), INTERVAL 1 MONTH ) AND ( price > 0 OR sales > 10000 ) GROUP BY id HAVING ( ( MAX( sales ) - MIN( sales ) ) * AVG( price ) ) * 30 / DATEDIFF( MAX( date ), MIN(date) )  > 200 OR MAX( SALES ) > 10000" );
			}

		} catch ( SQLException e ) {
			Log.exit( e );
		}

		return null != ret ? ret.iterator() : new ArrayList<Map<String, String>>().iterator();
	}

	public void saveData( HashMap<String, String> pageData ) {
		String q = "sqlite".equals( mode ) ?
		"INSERT INTO " + MODULES_TABLE + " (id, name, price, sales, date_added, date_modified, date) VALUES (?, ?, ?, ?, ?, ?, date('now') )" :
		"INSERT INTO " + MODULES_TABLE + " (id, name, price, sales, date_added, date_modified, date) VALUES (?, ?, ?, ?, ?, ?, NOW() )";

		try ( PreparedStatement s = connection.prepareStatement( q ) ) {
			s.setInt(    1, Integer.valueOf( pageData.get( "id" ) ) );
			s.setString( 2, pageData.get( "name" ) );
			s.setDouble( 3, Double.valueOf( pageData.getOrDefault( "price", "0" ) ) );
			s.setInt(    4, Integer.valueOf( pageData.get( "sales" ) ) );
			s.setString( 5, pageData.get( "dateAdded" ) );
			s.setString( 6, pageData.get( "dateModified" ) );
			s.executeUpdate();

		} catch ( SQLException e ) {
			Log.exit( "scanner_db::saveData: " + e.getMessage() );

		} catch ( NumberFormatException e ) {
			// TODO: something need to be done with values like 1,200.20
		}
	}
}