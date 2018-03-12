package com.ua.advertikon.helper;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import java.util.*;

import com.ua.advertikon.helper.*;

public class db_helper {
	public Connection connection = null;
	public Statement statement = null;

	private String db       = "modules";
	private String user     = "root";
	private String password = "1";
	protected String mode   = "mysql";

	public db_helper() {
		connect();
	}

	public void connect() {
		switch( mode ) {
			case "sqlite":
				connectSQLite();
			break;
			case "mysql":
				connectMySQL();
			break;
		}
	}

	protected void connectSQLite() {
		try {
			Class.forName( "org.sqlite.JDBC" );

		} catch ( ClassNotFoundException e ) {
			Log.exit( "db_helper::connectSQLite: " + e.getMessage() );
		}

		close();

		try {
			connection = DriverManager.getConnection( "jdbc:sqlite:" + db );
			statement = connection.createStatement();

			Log.debug( "Open SQLite connection to " + db );

		} catch ( SQLException e ) {
			Log.exit( "db_helper::connectSQLite: " + e.getMessage() );
		}
	}

	protected void connectMySQL() {
		String url = "jdbc:mysql://localhost:3306/" + db;

		try {
			Class.forName( "com.mysql.jdbc.Driver" );

		} catch ( ClassNotFoundException e ) {
			Log.exit( "db_helper::connectMySQL: " + e.getMessage() );
		}

		close();

		try {
			connection = DriverManager.getConnection( url, user, password );
			statement = connection.createStatement();
			statement.execute( "SET @@session.sql_mode = 'TRADITIONAL'" );

			Log.debug( "Open MySql connection to " + db + " for user " + user );

		} catch ( SQLException e ) {
			Log.exit( "db_helper::connectMySQL: " + e.getMessage() );
		}
	}

	synchronized public List<Map<String, String>> query( String q ) throws SQLException {
		// connect();
		return getData( statement.executeQuery( q ) );
	}

	synchronized public List<Map<String, String>> query( String q, String[] args ) throws SQLException {
		// connect();
		PreparedStatement s = connection.prepareStatement( q );
		ResultSet rs = null;

		for ( int i = 1; i <= args.length; i++ ) {
			s.setString( i, args[ i ] );
		}

		s.close();

		return getData( s.executeQuery() );
	}

	public boolean exec( String q ) throws SQLException {
		return statement.execute( q );
	}

	public void close() {
		try {
			if ( null != statement ) {
				statement.close();
			}

			if ( null != connection ) {
				connection.close();
			}

		} catch ( SQLException e ) {
			Log.exit( "db_helper::close: " + e.getMessage() );
		}
	}

	/**
	 * Concerts ResultSet into List of HashMaps
	 * @param {ResultSet} rs Result set
	 * @return {List} List
	 */
	protected List<Map<String, String>> getData( ResultSet rs ) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		List<Map<String, String>> rows = new ArrayList<Map<String, String>>();

		try {
			while ( rs.next() ) {
				Map<String, String> row = new HashMap<String, String>( columns );

				for( int i = 1; i <= columns; ++i ) {
					row.put( md.getColumnName( i ), rs.getString( i ) );
				}

				rows.add( row );
			}
			
		} catch ( SQLException e ) {
			Log.error( "db_helper::getData: " + e );
		}

		return rows;
	}
}