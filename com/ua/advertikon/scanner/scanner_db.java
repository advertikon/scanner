package com.ua.advertikon.scanner;

import com.ua.advertikon.helper.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.*;

class scanner_db extends db_helper {
	final String MODULES_TABLE = "modules";
	String db_file = "./modules.db";

	scanner_db() {
		super( "./modules.db" );

		try{
			exec( "create table if not exists " + MODULES_TABLE + " (id integer, name text, price decimal, date_added text, date_modified text, sales integer, date text )" );

		} catch ( SQLException e ) {
			System.out.println( e );
			System.exit( 1 );
		}
	}

	public int getLastId() {
		int id = 0;

		try {
			ResultSet r = query( "SELECT MAX(id) as max from " + MODULES_TABLE );
			id = r.getInt( "max" );
			
		} catch ( SQLException e ) {
			System.out.println( e );
			System.exit( -1 );
		}

		return id;
	}

	public ResultSet getWorthModules() {
		ResultSet ret = null;

		try {
			// More than 5000 sales/downloads or more than 500$ per month
			ret = query( "SELECT id FROM " + MODULES_TABLE + " WHERE sales > 5000 OR ( ( sales * price ) / (julianday( 'now' ) - julianday( date_added ) ) ) > 5" );

		} catch ( SQLException e ) {
			System.out.println( e );
			System.exit( 1 );
		}

		return ret;
	}

	public void saveData( HashMap<String, String> pageData ) {
		String q = "INSERT INTO " + MODULES_TABLE + " (id, name, price, sales, date_added, date_modified, date) VALUES (?, ?, ?, ?, ?, ?, date('now') )";

		try ( PreparedStatement s = connection.prepareStatement( q ) ) {
			s.setInt(    1, Integer.valueOf( pageData.get( "id" ) ) );
			s.setString( 2, pageData.get( "name" ) );
			s.setDouble( 3, Double.valueOf( pageData.getOrDefault( "price", "0" ) ) );
			s.setInt(    4, Integer.valueOf( pageData.get( "sales" ) ) );
			s.setString( 5, pageData.get( "dateAdded" ) );
			s.setString( 6, pageData.get( "dateModified" ) );
			s.executeUpdate();

		} catch ( SQLException e ) {
			System.out.println( e );
			System.exit( 2 );

		} catch ( NumberFormatException e ) {
			// TODO: something need to be done with values like 1,200.20
		}
	}
}