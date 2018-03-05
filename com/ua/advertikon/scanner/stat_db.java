package com.ua.advertikon.scanner;

import com.ua.advertikon.helper.*;

import java.time.*;

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
			String q = "SELECT *, ( ( MAX( sales ) - MIN( sales ) ) * AVG( price ) ) as profits FROM " + TABLE + " GROUP by id HAVING price > 0 AND profits > " + data.profits + " AND ";

			if ( !data.period.equals( "" ) ) {
				if ( 0 == data.period.compareToIgnoreCase( "week" ) ) {
					from = "-7 days";

				} else {
					from = "-1 " + data.period;
				}

				q += "date > date( 'now', '" + from + "')";

			} else {
				q += "date >= date('" + data.dateFrom + "') AND date <= date('" + data.dateTo + "')";
			}

			q += " LIMIT " + data.limit;

			System.out.println( q );

			try {
				Instant start = Instant.now();
				ret = query( q );
				System.out.println( "DQ query: " + Duration.between( start, Instant.now() ).toMillis() + " msec" );
				
			} catch ( SQLException e ) {
				System.out.println( "stat_db::getStatisticData: " + e.getMessage() );
			}


		} catch ( Exception e ) {
			System.out.println( "stat_db::getStatisticData: " + e.getMessage() );
		}

		return ret;
	}
}