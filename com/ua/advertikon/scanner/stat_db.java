package com.ua.advertikon.scanner;

import com.ua.advertikon.helper.*;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.*;

public class stat_db extends db_helper {
	final String TABLE = "modules";
	String db_file = "./modules.db";

	stat_db() {
		super( "modules.db" );
	}

	ResultSet getStatisticData( QueryData data ) {
		ResultSet ret = null;
		String from = "";
		String to = "";

		try {
			String q = "SELECT * FROM " + TABLE + " WHERE ";

			if ( !data.period.equals( "" ) ) {
				if ( 0 == data.period.compareToIgnoreCase( "week" ) ) {
					from = "-7 days";

				} else {
					from = "-1 " + data.period;
				}

				q+= "date > date( 'now', '" + from + "')";
			}

			q += " LIMIT 50";

			System.out.println( q );

			try {
				ret = query( q );
				
			} catch ( SQLException e ) {
				System.out.println( "stat_db::getStatisticData: " + e.getMessage() );
			}


		} catch ( Exception e ) {
			System.out.println( "stat_db::getStatisticData: " + e.getMessage() );
		}

		return ret;
	}
}