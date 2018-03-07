package com.ua.advertikon.helper;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
// import org.sqlite.JDBC;

public class db_helper {
	public Connection connection = null;
	public Statement statement = null;

	private String db       = "";
	private String user     = "";
	private String password = "";
	private String mode = "sqlite";

	public db_helper( String db ) {
		if ( db.equals( "" ) ) {
			System.out.println( "DB_helper: database name is missing" );
			System.exit( 1 );
		}

		this.db = db;
		connect();
	}

	public db_helper( String db, String user, String password ) {
		if ( db.equals( "" ) ) {
			System.out.println( "DB_helper: database name is missing" );
			System.exit( 1 );
		}

		this.db = db;
		this.user = user;
		this.password = password;
		mode = "mysql";
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

			connection = DriverManager.getConnection( "jdbc:sqlite:" + db );
			statement = connection.createStatement();

			System.out.println( "Open SQLite connection to " + db );

		} catch ( SQLException e ) {
			System.out.println( e );
			System.exit( 1 );
		}
	}

	protected void connectMySQL() {
		String url = "jdbc:mysql://localhost:3306/" + db;

		// try {
		// 	Class.forName( "org.sqlite.JDBC" );

		// } catch ( ClassNotFoundException e ) {
		// 	System.out.println( e );
		// 	System.exit( -1 );
		// }

		try {
			if ( null != statement ) {
				statement.close();
			}

			if ( null != connection ) {
				connection.close();
			}

			connection = DriverManager.getConnection( url, user, password );
			statement = connection.createStatement();

			System.out.println( "Open SQLite connection to " + db + " for user " + user );

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