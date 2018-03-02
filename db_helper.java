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

	db_helper( String db_file ) {

		try {
			Class.forName( "org.sqlite.JDBC" );

		} catch ( ClassNotFoundException e ) {
			System.out.println( e );
			System.exit( -1 );
		}

		if ( db_file.equals( "" ) ) {
			System.out.println( "DB_helper: database file name is missing" );
			System.exit( 1 );
		}

		try {
			connection = DriverManager.getConnection( "jdbc:sqlite:" + db_file );
			statement = connection.createStatement();

		} catch ( SQLException e ) {
			System.out.println( e );
			System.exit( 1 );
		}
	}

	// protected void finalize() {
	// 	try {
	// 		if ( null != connection ) {
	// 			connection.close();
	// 		}

	// 		if ( null != statement ) {
	// 			statement.close();
	// 		}
			
	// 	} catch ( SQLException e ) {
	// 		System.out.println( e );
	// 	}
	// }
}