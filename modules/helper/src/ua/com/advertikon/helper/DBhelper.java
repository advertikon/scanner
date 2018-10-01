package ua.com.advertikon.helper;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import com.mysql.jdbc.Driver;
import java.time.Duration;
import java.time.Instant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DBhelper {
	protected Connection connection;
	protected Statement statement = null;

	final private String db       = "modules";
	final private String user     = "root";
	final private String password = "1";
	final protected String mode   = "mysql";

	public DBhelper() {
        this.connection = null;
        connect();
	}

	/**
	 *
	 */
	final public void connect() {
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
			Logger.getLogger(DBhelper.class.getName()).log(Level.SEVERE, null, e);
		}

		close();

		try {
			setConnection(DriverManager.getConnection( "jdbc:sqlite:" + db ));
			setStatement(getConnection().createStatement());

			Log.debug( "Open SQLite connection to " + db );

		} catch ( SQLException e ) {
			Log.exit( "db_helper::connectSQLite: " + e.getMessage() );
		}
	}

	protected void connectMySQL() {
		String url = "jdbc:mysql://localhost:3306/" + db;
		close();

		try {
			setConnection(DriverManager.getConnection( url, user, password ));
			setStatement(getConnection().createStatement());
			getStatement().execute( "SET @@session.sql_mode = 'TRADITIONAL'" );

		} catch ( SQLException e ) {
			Logger.getLogger(DBhelper.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	final synchronized public List<Map<String, String>> query( String q ) throws SQLException {
		Instant start = Instant.now();
		ResultSet rs = getStatement().executeQuery( q );
		System.out.format( "%s\n%d msec\n", q, Duration.between( start, Instant.now() ).toMillis() );
		return getData( rs );
	}

	final synchronized public List<Map<String, String>> query( String q, String[] args ) throws SQLException {
		// connect();
		PreparedStatement s = getConnection().prepareStatement( q );
		ResultSet rs = null;

		for ( int i = 1; i <= args.length; i++ ) {
			s.setString( i, args[ i ] );
		}

		s.close();

		return getData( s.executeQuery() );
	}

	final public boolean exec( String q ) throws SQLException {
		return getStatement().execute( q );
	}

	public void close() {
		try {
			if ( null != getStatement() ) {
				getStatement().close();
			}

			if ( null != getConnection() ) {
				getConnection().close();
			}

		} catch ( SQLException e ) {
			Logger.getLogger(DBhelper.class.getName()).log(Level.SEVERE, null, e);
		}
	}

	/**
	 * Concerts ResultSet into List of HashMaps
	 * @param rs
	 * @return {List} List
	 * @throws java.sql.SQLException
	 */
	protected List<Map<String, String>> getData( ResultSet rs ) throws SQLException {
		ResultSetMetaData md = rs.getMetaData();
		int columns = md.getColumnCount();
		List<Map<String, String>> rows = new ArrayList<>();

		try {
			while ( rs.next() ) {
				Map<String, String> row = new HashMap<>( columns );

				for( int i = 1; i <= columns; ++i ) {
					row.put( md.getColumnName( i ), rs.getString( i ) );
				}

				rows.add( row );
			}
			
		} catch ( SQLException e ) {
			Logger.getLogger(DBhelper.class.getName()).log(Level.SEVERE, null, e);
		}
		
		System.out.println( "Rows count: " + rows.size() );

		return rows;
	}

    /**
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * @param connection the connection to set
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * @return the statement
     */
    public Statement getStatement() {
        return statement;
    }

    /**
     * @param statement the statement to set
     */
    public void setStatement(Statement statement) {
        this.statement = statement;
    }
}