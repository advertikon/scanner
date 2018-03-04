package com.ua.advertikon.helper;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.sqlite.JDBC;

public class db_helper {
	public Connection connection = null;
	public Statement statement = null;

	protected String db_file = "";

	public db_helper( String db_file ) {
		if ( db_file.equals( "" ) ) {
			System.out.println( "DB_helper: database file name is missing" );
			System.exit( 1 );
		}

		this.db_file = db_file;
		connect();
	}

	public void connect() {
		try {
			Class.forName( "org.sqlite.JDBC" );

		} catch ( ClassNotFoundException e ) {
			System.out.println( e );
			System.exit( -1 );
		}

		try {
			if ( null != statement ) {
				statement.close();
			}

			if ( null != connection ) {
				connection.close();
			}

			connection = DriverManager.getConnection( "jdbc:sqlite:" + db_file );
			statement = connection.createStatement();

		} catch ( SQLException e ) {
			System.out.println( e );
			System.exit( 1 );
		}
	}

	public ResultSet query( String q ) throws SQLException {
		ResultSet rs = null;
		// connect();
		rs = statement.executeQuery( q );

		return rs;
	}

	public boolean exec( String q ) throws SQLException {
		return statement.execute( q );
	}
}