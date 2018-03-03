import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.*;

class stat_db extends db_helper {
	final String TABLE = "modules";
	String db_file = "./modules.db";

	stat_db() {
		super( "modules.db" );
	}

	ResultSet getStatisticData( QueryData data ) {
		ResultSet ret = null;
		connect();

		try {
			String q = "SELECT * FROM " + TABLE + " WHERE ";

			if ( !data.period.equals( "" ) ) {
				q+= "date > date( 'now', '-1 " + data.period + "')";
			}

			q += " LIMIT 50";

			System.out.println( q );

			ret = statement.executeQuery( q );

		} catch ( SQLException e ) {
			System.out.println( "stat_db::getStatisticData: " + e.getMessage() );
		}

		return ret;
	}
}