import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

class db_helper {
	ResultSet ids = null;
	Connection connection = null;
	Statement statement = null;

	protected String db_file = "";

	db_helper( String db_file ) {
		if ( db_file.equals( "" ) ) {
			System.out.println( "DB_helper: database file name is missing" );
			System.exit( 1 );
		}

		this.db_file = db_file;
	}

	protected void connect() {
		try {
			Class.forName( "org.sqlite.JDBC" );

		} catch ( ClassNotFoundException e ) {
			System.out.println( e );
			System.exit( -1 );
		}

		try {
			if ( null != statement ) {
				statement.close();

				System.out.println( "Close statement" );
			}

			if ( null != connection ) {
				connection.close();

				System.out.println( "close connection" );
			}

			connection = DriverManager.getConnection( "jdbc:sqlite:" + db_file );
			statement = connection.createStatement();

			System.out.println( "Connection: " + connection );
			System.out.println( "Statement: " + statement );

			System.out.println( "Open connection" );

		} catch ( SQLException e ) {
			System.out.println( e );
			System.exit( 1 );
		}
	}
}